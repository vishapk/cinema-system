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

public data class AvailableTicketsParams(
  public val title: String?
)

public class AvailableTicketsParamSetter : ParamSetter<AvailableTicketsParams> {
  public override fun map(ps: PreparedStatement, params: AvailableTicketsParams): Unit {
    ps.setObject(1, params.title)
  }
}

public data class AvailableTicketsResult(
  public val startTime: Timestamp,
  public val availableTickets: Int,
  public val id: Int,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val price: Int?
)

public class AvailableTicketsRowMapper : RowMapper<AvailableTicketsResult> {
  public override fun map(rs: ResultSet): AvailableTicketsResult = AvailableTicketsResult(
  startTime = rs.getObject("start_time") as java.sql.Timestamp,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    id = rs.getObject("id") as kotlin.Int,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class AvailableTicketsQuery : Query<AvailableTicketsParams, AvailableTicketsResult> {
  public override val sql: String = """
      |SELECT shows.start_time , shows.available_tickets, shows.id, shows.end_time, shows.movie_id, shows.capacity,shows.price FROM shows
      |    INNER JOIN movies ON shows.movie_id =movies.id and movies.title like ?
      |    WHERE shows.available_tickets >= 0
      |    ORDER BY start_time ASC
      |
      |""".trimMargin()

  public override val mapper: RowMapper<AvailableTicketsResult> = AvailableTicketsRowMapper()

  public override val paramSetter: ParamSetter<AvailableTicketsParams> =
      AvailableTicketsParamSetter()
}
