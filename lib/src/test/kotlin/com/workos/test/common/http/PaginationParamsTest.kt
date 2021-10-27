package com.workos.test.common.http

import com.workos.common.http.PaginationParams
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationParamsTest {

  @Test
  fun paginationParamsCanBeInstantiatedDirectly() {
    var params = PaginationParams(after = "after")
    assertEquals(params["after"], "after")
    assertEquals(params["before"], null)
    assertEquals(params["limit"], null)

    params = PaginationParams(before = "before")
    assertEquals(params["after"], null)
    assertEquals(params["before"], "before")
    assertEquals(params["limit"], null)

    params = PaginationParams(limit = 10)
    assertEquals(params["after"], null)
    assertEquals(params["before"], null)
    assertEquals(params["limit"], "10")

    params = PaginationParams("after", "before", 10)
    assertEquals(params["after"], "after")
    assertEquals(params["before"], "before")
    assertEquals(params["limit"], "10")
  }

  @Test
  fun paginationParamsBuilderShouldBuildAMap() {
    var params = PaginationParams.builder()
      .after("after")
      .before("before")
      .limit(10)
      .build()

    assertTrue(params is MutableMap<String, String>)
  }

  @Test
  fun paginationParamsBuilderShouldBuildTheExpectedParams() {
    var params = PaginationParams.builder()
      .after("after")
      .before("before")
      .limit(10)
      .build()

    assertEquals(params["after"], "after")
    assertEquals(params["before"], "before")
    assertEquals(params["limit"], "10")
  }
}
