package com.workos.test.webhooks

import com.workos.directorysync.models.DirectoryType
import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class DirectoryWebhookTests : TestBase() {

  private val directoryId = "directory_01FMXJ1RRC9C7B4AR001TBBBHR"

  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateGroupWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "id": "$directoryId",
        "name": "F",
        "type": "gsuite directory",
        "state": "linked",
        "object": "directory",
        "created_at": "2021-11-20T10:15:50.695Z",
        "updated_at": "2021-11-20T10:16:26.921Z",
        "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
      },
      "event": "${eventType.value}",
      "created_at": "2021-11-20T10:15:50.695Z"
    }
    """
  }

  @Test
  fun constructDirectoryActivatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.DirectoryActivated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryActivatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryActivatedEvent).data.id, directoryId)
  }

  @Test
  fun constructDirectoryActivatedEventWithUnknownDirectoryType() {
    val workos = createWorkOSClient()
    val webhookData =
      """
      {
        "id": "$webhookId",
        "data": {
          "id": "$directoryId",
          "name": "F",
          "type": "unknown directory",
          "state": "linked",
          "object": "directory",
          "created_at": "2021-11-20T10:15:50.695Z",
          "updated_at": "2021-11-20T10:16:26.921Z",
          "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
        },
        "event": "${EventType.DirectoryActivated.value}",
        "created_at": "2021-11-20T10:15:50.695Z"
      }
      """
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryActivatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryActivatedEvent).data.id, directoryId)
    assertEquals((webhook as DirectoryActivatedEvent).data.type, DirectoryType.Unknown)
  }

  @Test
  fun constructDirectoryDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.DirectoryDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryDeletedEvent).data.id, directoryId)
  }
}
