package my.database.databasebrokerservice.services

import io.grpc.examples.helloworld.AuthGrpc
import io.grpc.examples.helloworld.TokenRequest
import io.grpc.examples.helloworld.TokenResponse
import jakarta.annotation.PostConstruct
import lombok.AllArgsConstructor
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

@Service
@AllArgsConstructor
class gRPClient {
    @GrpcClient("Auth")
    private lateinit var AuthStub: AuthGrpc.AuthBlockingStub

    fun validateToken(token: String): TokenResponse {
        val message= TokenRequest.newBuilder().setToken(token).build()
        val response = AuthStub.validate(message)
        return response
    }
}