{%- from 'monitoring/settings.sls' import monitoring with context %}

{%- if monitoring.enabled and monitoring.nodeExporterExists %}

/var/lib/node_exporter/scripts/salt.sh:
  file.managed:
    - source: salt://monitoring/textfiles/salt.sh
    - user: "root"
    - group: "root"
    - mode: 700

/var/lib/node_exporter/scripts/salt-key.sh:
  file.managed:
    - source: salt://monitoring/textfiles/salt-key.sh
    - user: "root"
    - group: "root"
    - mode: 700
    - onlyif: test -d /srv/salt

/var/lib/node_exporter/scripts/logging-agent.sh:
  file.managed:
    - source: salt://monitoring/textfiles/logging-agent.sh.j2
    - user: "root"
    - group: "root"
    - template: jinja
    - mode: 700

salt_cron:
  cron.present:
    - name: /var/lib/node_exporter/scripts/salt.sh
    - user: root
    - minute: '*/5'
    - require:
        - file: /var/lib/node_exporter/scripts/salt.sh

salt_key_cron:
  cron.present:
    - name: /var/lib/node_exporter/scripts/salt-key.sh
    - user: root
    - minute: '*/20'
    - require:
        - file: /var/lib/node_exporter/scripts/salt-key.sh

logging_agent_cron:
  cron.present:
    - name: /var/lib/node_exporter/scripts/logging-agent.sh
    - user: root
    - minute: '*/20'
    - require:
        - file: /var/lib/node_exporter/scripts/logging-agent.sh

{%- endif %}