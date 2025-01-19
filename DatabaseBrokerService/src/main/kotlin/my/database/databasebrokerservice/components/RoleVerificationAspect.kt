package my.database.databasebrokerservice.components

import jakarta.servlet.http.HttpServletResponse
import my.database.databasebrokerservice.annotations.RequiresRole
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
@Aspect
@Component
class RoleVerificationAspect {

    @Around("@annotation(requiresRole)")
    fun verifyRole(joinPoint: ProceedingJoinPoint, requiresRole: RequiresRole): Any? {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val userRole = request.getAttribute("userRole") as? String
        if (userRole == null || !requiresRole.role.contains(userRole)) {
            val response = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response
            response?.status = HttpServletResponse.SC_FORBIDDEN
            response?.writer?.write("Forbidden")
            return null
        }
        return joinPoint.proceed()
    }
}