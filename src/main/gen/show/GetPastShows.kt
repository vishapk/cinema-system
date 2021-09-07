package show

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public class GetPastShowsParams

public class GetPastShowsParamSetter : ParamSetter<GetPastShowsParams> {
  public override fun map(ps: PreparedStatement, params: GetPastShowsParams): Unit {
  }
}

public data class GetPastShowsResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val availableTickets: Int,
  public val price: Int?
)

public class GetPastShowsRowMapper : RowMapper<GetPastShowsResult> {
  public override fun map(rs: ResultSet): GetPastShowsResult = GetPastShowsResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class GetPastShowsQuery : Query<GetPastShowsParams, GetPastShowsResult> {
  public override val sql: String = """
      |SELECT * FROM shows s
      |WHERE s.end_time < current_timestamp
      |ORDER BY start_time asc
      |""".trimMargin()

  public override val mapper: RowMapper<GetPastShowsResult> = GetPastShowsRowMapper()

  public override val paramSetter: ParamSetter<GetPastShowsParams> = GetPastShowsParamSetter()
}
