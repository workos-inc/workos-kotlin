package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class DirectoryList
@JsonCreator constructor(
  val data: List<Directory>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
