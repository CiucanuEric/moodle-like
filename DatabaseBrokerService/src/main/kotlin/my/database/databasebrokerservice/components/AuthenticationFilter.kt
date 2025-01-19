package my.database.databasebrokerservice.components

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import my.database.databasebrokerservice.services.ValidateToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthenticationFilter(
    private val validateToken: ValidateToken
) : OncePerRequestFilter() {


    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val requestUri = request.requestURI
        return requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")
    }
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        val tokenResponse = validateToken.isValidToken(authHeader)
        if (tokenResponse == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Invalid token")
            return
        }

        val userRole = tokenResponse.role
        val userSub = tokenResponse.sub

        if (userRole == null || userSub == null) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.writer.write("Role or subject is missing in token")
            return
        }
        request.setAttribute("userRole", userRole)
        request.setAttribute("userSub", userSub)

        filterChain.doFilter(request, response)
    }
}