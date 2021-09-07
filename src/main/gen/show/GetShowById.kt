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

public data class GetShowByIdParams(
  public val showId: Int?
)

public class GetShowByIdParamSetter : ParamSetter<GetShowByIdParams> {
  public override fun map(ps: PreparedStatement, params: GetShowByIdParams): Unit {
    ps.setObject(1, params.showId)
  }
}

public data class GetShowByIdResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val availableTickets: Int,
  public val price: Int?
)

public class GetShowByIdRowMapper : RowMapper<GetShowByIdResult> {
  public override fun map(rs: ResultSet): GetShowByIdResult = GetShowByIdResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class GetShowByIdQuery : Query<GetShowByIdParams, GetShowByIdResult> {
  public override val sql: String = "SELECT * from shows where id=?;"

  public override val mapper: RowMapper<GetShowByIdResult> = GetShowByIdRowMapper()

  public override val paramSetter: ParamSetter<GetShowByIdParams> = GetShowByIdParamSetter()
}
