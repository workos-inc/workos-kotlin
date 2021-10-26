package com.workos.sso

import com.workos.common.exceptions.UnauthorizedException
import com.workos.sso.models.ConnectionType
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class SSOTest : TestBase() {
    class ExampleResponseType {
        val message: String = ""
    }

    class ExampleRequestEntity {
        val param: String = ""
    }

    @Test
    fun deleteConnectionShouldNotError() {
        val workos = createWorkOSClient()

        val id = "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

        stubResponse(
            "/connections/$id",
            "{}"
        )

        val response = workos.sso.deleteConnection(id)

        assertEquals(Unit, response)
    }

    @Test
    fun deleteConnectionShouldThrowError() {
        val workos = createWorkOSClient()

        val id = "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

        stubResponse(
            "/connections/$id",
            "{}",
            401
        )

        assertThrows(UnauthorizedException::class.java) {
            workos.sso.deleteConnection(id)
        }
    }

    @Test
    fun getAuthorizationURLShouldReturnValidURL() {
        val workos = createWorkOSClient()

        val url = workos.sso.getAuthorizationURL("client_id", "http://localhost:8080/redirect").build()

        assertEquals(
            "http://localhost:8081/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code",
            url
        )
    }

    @Test
    fun getAuthorizationURLShouldAcceptAdditionalParams() {
        val workos = createWorkOSClient()

        val url = workos.sso.getAuthorizationURL("client_id", "http://localhost:8080/redirect")
            .connection("connection_value")
            .domain("domain_value")
            .provider("provider_value")
            .state("state_value")
            .build()

        assertEquals(
            "http://localhost:8081/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&connection=connection_value&domain=domain_value&provider=provider_value&state=state_value",
            url
        )
    }

    @Test
    fun getConnectionShouldReturnConnection() {
        val workos = createWorkOSClient()

        val id = "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

        stubResponse(
            "/connections/$id",
            """{
            "connection_type": "GoogleOAuth",
            "created_at": "2021-10-26 13:29:47.133382",
            "domains": [],
            "id": "$id",
            "name": "Google OAuth 2.0",
            "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
            "state": "active",
            "updated_at": "2021-10-26 13:29:47.133382"
        }"""
        )

        val connection = workos.sso.getConnection(id)

        assertEquals(id, connection.id)
        assertEquals(ConnectionType.GoogleOAuth, connection.connectionType)
    }
}
