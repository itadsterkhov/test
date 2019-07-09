package com.itad.autorepaircloud.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MediaModel {
    private Long fileCreated;
    private String fileFormat;
    private String fileLocation;
    private Integer fileSize;
    private String mediaType;
    private String recordId;
    private String recordTable;
    private Integer sequenceNum;
    private String status;
}
