package mongodb.lecturesservice.services

import io.grpc.examples.helloworld.TokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ValidateToken {

    @Autowired
    private lateinit var gRPClient: gRPClient

    companion object
    {
        val INVALID = "invalid"
        val EXPIRED="expired"
        val WRONG_ISSUER="wrong_issuer"
    }
    fun isValidToken(authHeader: String?): TokenResponse?  {
        if(authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
            return null
        }
        val token = authHeader.substring(7)
        val response=gRPClient.validateToken(token)
        if(response.role == EXPIRED || response.role== WRONG_ISSUER || response.sub.equals("none")) {
            return null
        }
        return response
    }
}
