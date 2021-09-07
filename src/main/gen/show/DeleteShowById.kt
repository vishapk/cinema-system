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

public data class DeleteShowByIdParams(
  public val show_id: Int?
)

public class DeleteShowByIdParamSetter : ParamSetter<DeleteShowByIdParams> {
  public override fun map(ps: PreparedStatement, params: DeleteShowByIdParams): Unit {
    ps.setObject(1, params.show_id)
  }
}

public data class DeleteShowByIdResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val availableTickets: Int,
  public val price: Int?
)

public class DeleteShowByIdRowMapper : RowMapper<DeleteShowByIdResult> {
  public override fun map(rs: ResultSet): DeleteShowByIdResult = DeleteShowByIdResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class DeleteShowByIdQuery : Query<DeleteShowByIdParams, DeleteShowByIdResult> {
  public override val sql: String = """
      |DELETE FROM shows s
      |WHERE s.id = ?
      |RETURNING *;
      |""".trimMargin()

  public override val mapper: RowMapper<DeleteShowByIdResult> = DeleteShowByIdRowMapper()

  public override val paramSetter: ParamSetter<DeleteShowByIdParams> = DeleteShowByIdParamSetter()
}
