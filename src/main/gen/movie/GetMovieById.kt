package movie

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetMovieByIdParams(
  public val id: Int?
)

public class GetMovieByIdParamSetter : ParamSetter<GetMovieByIdParams> {
  public override fun map(ps: PreparedStatement, params: GetMovieByIdParams): Unit {
    ps.setObject(1, params.id)
  }
}

public data class GetMovieByIdResult(
  public val id: Int,
  public val title: String,
  public val durationInMinutes: Int
)

public class GetMovieByIdRowMapper : RowMapper<GetMovieByIdResult> {
  public override fun map(rs: ResultSet): GetMovieByIdResult = GetMovieByIdResult(
  id = rs.getObject("id") as kotlin.Int,
    title = rs.getObject("title") as kotlin.String,
    durationInMinutes = rs.getObject("duration_in_minutes") as kotlin.Int)
}

public class GetMovieByIdQuery : Query<GetMovieByIdParams, GetMovieByIdResult> {
  public override val sql: String = """
      |SELECT * FROM movies
      |WHERE id = ?
      |""".trimMargin()

  public override val mapper: RowMapper<GetMovieByIdResult> = GetMovieByIdRowMapper()

  public override val paramSetter: ParamSetter<GetMovieByIdParams> = GetMovieByIdParamSetter()
}
