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

public class GetOngoingShowsParams

public class GetOngoingShowsParamSetter : ParamSetter<GetOngoingShowsParams> {
  public override fun map(ps: PreparedStatement, params: GetOngoingShowsParams): Unit {
  }
}

public data class GetOngoingShowsResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val availableTickets: Int,
  public val price: Int?
)

public class GetOngoingShowsRowMapper : RowMapper<GetOngoingShowsResult> {
  public override fun map(rs: ResultSet): GetOngoingShowsResult = GetOngoingShowsResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class GetOngoingShowsQuery : Query<GetOngoingShowsParams, GetOngoingShowsResult> {
  public override val sql: String = """
      |select * from shows s
      |where current_timestamp between s.start_time and s.end_time
      |ORDER BY start_time asc
      |""".trimMargin()

  public override val mapper: RowMapper<GetOngoingShowsResult> = GetOngoingShowsRowMapper()

  public override val paramSetter: ParamSetter<GetOngoingShowsParams> = GetOngoingShowsParamSetter()
}
