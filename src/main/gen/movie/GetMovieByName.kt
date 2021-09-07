package movie

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetMovieByNameParams(
  public val title: String?
)

public class GetMovieByNameParamSetter : ParamSetter<GetMovieByNameParams> {
  public override fun map(ps: PreparedStatement, params: GetMovieByNameParams): Unit {
    ps.setObject(1, params.title)
  }
}

public data class GetMovieByNameResult(
  public val id: Int,
  public val title: String,
  public val durationInMinutes: Int
)

public class GetMovieByNameRowMapper : RowMapper<GetMovieByNameResult> {
  public override fun map(rs: ResultSet): GetMovieByNameResult = GetMovieByNameResult(
  id = rs.getObject("id") as kotlin.Int,
    title = rs.getObject("title") as kotlin.String,
    durationInMinutes = rs.getObject("duration_in_minutes") as kotlin.Int)
}

public class GetMovieByNameQuery : Query<GetMovieByNameParams, GetMovieByNameResult> {
  public override val sql: String = """
      |SELECT * FROM movies
      |WHERE title = ?
      |""".trimMargin()

  public override val mapper: RowMapper<GetMovieByNameResult> = GetMovieByNameRowMapper()

  public override val paramSetter: ParamSetter<GetMovieByNameParams> = GetMovieByNameParamSetter()
}
