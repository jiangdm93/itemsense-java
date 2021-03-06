package com.impinj.itemsense.client.coordinator.readerhealth;

import com.google.gson.annotations.SerializedName;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReaderVersion {
    @JsonProperty("App")
    @SerializedName("App")
    private String app;

    @JsonProperty("Firmware")
    @SerializedName("Firmware")
    private String firmware;
}
