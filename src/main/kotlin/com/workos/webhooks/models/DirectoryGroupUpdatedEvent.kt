package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Webhook Event for `dsync.group.updated`.
 */
class DirectoryGroupUpdatedEvent
@JsonCreator constructor(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: GroupUpdated
) : WebhookEvent(id, event, data)
