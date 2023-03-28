package jth.grpctest

import android.net.Uri
import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import jth.grpctest.bookinfo.BookInfoRequest
import jth.grpctest.bookinfo.BookInfoServiceGrpcKt
import java.io.*


class BookInfoRCP(uri: Uri) : Closeable {
    companion object {
        private const val ADDRESS = "0.0.0.0"
        private const val PORT = 8080
    }

    private var managedChannel: ManagedChannel? = null

    @Synchronized
    private fun getManagedChannel(): ManagedChannel? {
        if (managedChannel == null) {
            managedChannel = ManagedChannelBuilder
                .forAddress("146.56.146.184", 8080)
                .useTransportSecurity()
                .usePlaintext()
                .build()
        }
        return managedChannel
    }

    override fun close() {
        getManagedChannel()?.shutdown()
    }

    private suspend fun getBookInfo() {
        getManagedChannel()?.let { channel ->
            runCatching {
                val stub = BookInfoServiceGrpcKt.BookInfoServiceCoroutineStub(channel)

                val input: InputStream = FileInputStream(File("file.txt"))
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var n: Int = input.read(buffer)

                while (n != -1) {
                    output.write(buffer, 0, n)
                    n = input.read(buffer)
                }

                stub.getBookInfo(
                    BookInfoRequest.newBuilder().setFile(ByteString.copyFrom(output.toByteArray()))
                        .build()
                )
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}