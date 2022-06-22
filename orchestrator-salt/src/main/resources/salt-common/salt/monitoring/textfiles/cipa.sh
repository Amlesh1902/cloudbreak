#!/bin/bash

function parse_table() {
  # first awk: split table by | char from the second line and skip lines that starts with and print columns as comma separated values +--
  # last awk: the first column (comma separated) -> lowercase + replace space with underscore
  # output: comma separated fields based on thw formatted table
  cat /tmp/cipa_out.txt | awk -F'|' ' NR > 1 && !/^+--/ {printf $2;for(i=3;i<NF;++i)printf ","$i; printf "\n"}' | awk '{$1=$1};1' | sed 's/\s*,\s*/,/g' | grep -v '^,*$' | awk  -F',' '{l=tolower($1); un=gsub(/ /,"_",l); printf l; for(i=2;i<=NF;++i)printf ","$i; printf "\n" }'
}

function process_records_with_header() {
  # initialize number of servers and state index based on the first parsed line, then process the next lines by write_metric_records function
  local parsed_lines=$1
  idx=0
  servers_num=1
  state_index=2
  server_names=()
  for line in ${parsed_lines} ; do
    if [ "$idx" == "0" ]; then
      comma_num=$(($(echo "$line" | tr -cd , | wc -c)))
      servers_num=$((comma_num - 1))
      state_index=$((servers_num + 1))
      IFS=',' read -ra ROW_ARR <<< "$line"
      server_act_idx=0
      for col in ${ROW_ARR[@]} ; do
        if [[ "$server_act_idx" != "0" && "$server_act_idx" != "$((state_index))" ]]; then
          server_names+=($col)
        fi
        server_act_idx=$((server_act_idx+1))
      done
      touch $prom_tmp_file
    else
      write_metric_records "${line}" "${state_index}" "${server_names[*]}"
    fi
    idx=$((idx + 1))
  done
}

function write_metric_records() {
  # write metrics from comma separated cipa data to prom file in open metrics format
  # if the first column empty: skip
  # if the first column is replication_status write only the status as a metrics
  # if the first column is not replication_status, write a status only metric, and an ipaserver specific value based metric
  # in case of a metric value is True/False -> transform metric to 0 / 1
  # in case of a metric value is not a number and not a True/False -> metric value will be 0, but cipa_info label will be filled as the value
  local line=$1
  local state_index=$2
  IFS=' ' read -r -a server_names <<< "$3"
  IFS=',' read -ra ROW_ARR <<< "$line"
  if [[ "${ROW_ARR[0]}" == "" ]]; then
    continue
  elif [[ "${ROW_ARR[0]}" == "replication_status" ]];then
    replication_status_metric_value=1
    if [[ "${ROW_ARR[$state_index]}" == "OK" ]]; then
      replication_status_metric_value=0
    fi
    echo "cipa_${ROW_ARR[0]}{cipa_status=\"${ROW_ARR[$state_index]}\"} $replication_status_metric_value" >> $prom_tmp_file
  else
    metric_name="cipa_${ROW_ARR[0]}"
    status_metric_name="cipa_${ROW_ARR[0]}_status"
    status_metric_value=1
    if [[ "${ROW_ARR[$state_index]}" == "OK" ]]; then
      status_metric_value=0
    fi
    echo "$status_metric_name{cipa_status=\"${ROW_ARR[$state_index]}\"} $status_metric_value" >> $prom_tmp_file
    vals_idx=0
    for col in ${ROW_ARR[@]} ; do
      if [[ "$vals_idx" != "0" && "$vals_idx" != "$state_index" ]]; then
        srv_idx_for_metric=$(($vals_idx - 1))
        server_name=${server_names[$srv_idx_for_metric]}
        if [ -z "${col##[0-9]*}" ]; then
          echo "$metric_name{cipa_server=\"$server_name\"} $col" >> $prom_tmp_file
        elif [[ "$col" == "True" ]]; then
          echo "$metric_name{cipa_server=\"$server_name\"} 0" >> $prom_tmp_file
        elif [[  "$col" == "False" ]]; then
          echo "$metric_name{cipa_server=\"$server_name\"} 1" >> $prom_tmp_file
        else
          echo "$metric_name{cipa_server=\"$server_name\", cipa_info=\"$col\"} 0" >> $prom_tmp_file
        fi
      fi
      vals_idx=$((vals_idx+1))
    done
  fi
}

prom_file="/var/lib/node_exporter/files/cipa.prom"
prom_tmp_file="${prom_file}.$$"

if [[ ! -f /var/run/salt-master.pid || ! -f /usr/local/bin/cipa ]]; then
  echo "[SKIP] Command only runs on salt-master (running) & cipa tool is required."
  exit 0;
fi

/usr/local/bin/cipa -d $(hostname -d) -W $(tail -n +2 /srv/pillar/freeipa/init.sls | jq -r '.freeipa.password') > /tmp/cipa_out.txt
result=$?
if [[ "$result" == "0" ]]; then
  parsed_lines=$(parse_table)
  process_records_with_header "${parsed_lines}"
  if [[ -f "$prom_tmp_file" && -s "$prom_tmp_file" ]]; then
    mv "${prom_tmp_file}" "${prom_file}"
  else
    rm -rf "${prom_file}.*"
  fi
fi