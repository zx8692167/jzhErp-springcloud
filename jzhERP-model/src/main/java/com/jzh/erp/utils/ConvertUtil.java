package com.jzh.erp.utils;

import org.apache.commons.io.IOUtils;
import com.google.common.net.HttpHeaders;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import feign.Response;

public class ConvertUtil {
    @SneakyThrows
    public static void feignResp2ServletResp(Response feignResponse, HttpServletResponse response) {
        Response.Body body = feignResponse.body();
        try (InputStream inputStream = body.asInputStream(); OutputStream outputStream = response.getOutputStream())
        {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, feignResponse.headers().get(HttpHeaders.CONTENT_DISPOSITION).stream().findFirst().get());
            response.setContentType(feignResponse.headers().get(HttpHeaders.CONTENT_TYPE).stream().findFirst().get());
            IOUtils.copy(inputStream, outputStream);
        }
    }
}
