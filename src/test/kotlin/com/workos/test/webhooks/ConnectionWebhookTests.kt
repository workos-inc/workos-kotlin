package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class ConnectionWebhookTests : TestBase() {

  private val connectionId = "conn_01FMXJ0YAP7JX3377YFV2XPCJE"

  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateGroupWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "id": "$connectionId",
        "name": "G",
        "state": "inactive",
        "object": "connection",
        "created_at": "2021-11-20T10:15:23.713Z",
        "updated_at": "2021-11-20T10:15:23.713Z",
        "connection_type": "GoogleSAML",
        "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
      },
      "event": "${eventType.value}",
      "created_at": "2021-11-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructDirectoryActivatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.ConnectionActivated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is ConnectionActivatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionActivatedEvent).data.id, connectionId)
  }

  @Test
  fun constructDirectoryDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.ConnectionDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is ConnectionDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionDeletedEvent).data.id, connectionId)
  }
}
