package com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.repository;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.common.model.JsonEntity;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions.ClusterManagerRepositoryDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RepositoryV4Request implements JsonEntity {

    @NotNull
    @ApiModelProperty(value = ClusterManagerRepositoryDescription.VERSION, required = true)
    private String version;

    @ApiModelProperty(ClusterManagerRepositoryDescription.BASE_URL)
    private String baseUrl;

    @ApiModelProperty(ClusterManagerRepositoryDescription.REPO_GPG_KEY)
    private String gpgKeyUrl;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getGpgKeyUrl() {
        return gpgKeyUrl;
    }

    public void setGpgKeyUrl(String gpgKeyUrl) {
        this.gpgKeyUrl = gpgKeyUrl;
    }

    @Override
    public String toString() {
        return "RepositoryV4Request{" +
                "version='" + version + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", gpgKeyUrl='" + gpgKeyUrl + '\'' +
                '}';
    }
}
