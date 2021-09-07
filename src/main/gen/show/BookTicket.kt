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

public data class BookTicketParams(
  public val show_id: Int?
)

public class BookTicketParamSetter : ParamSetter<BookTicketParams> {
  public override fun map(ps: PreparedStatement, params: BookTicketParams): Unit {
    ps.setObject(1, params.show_id)
  }
}

public data class BookTicketResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val availableTickets: Int,
  public val price: Int?
)

public class BookTicketRowMapper : RowMapper<BookTicketResult> {
  public override fun map(rs: ResultSet): BookTicketResult = BookTicketResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class BookTicketQuery : Query<BookTicketParams, BookTicketResult> {
  public override val sql: String = """
      |UPDATE shows SET available_tickets = available_tickets-1 WHERE id = ?
      |    returning *;
      |
      |""".trimMargin()

  public override val mapper: RowMapper<BookTicketResult> = BookTicketRowMapper()

  public override val paramSetter: ParamSetter<BookTicketParams> = BookTicketParamSetter()
}
