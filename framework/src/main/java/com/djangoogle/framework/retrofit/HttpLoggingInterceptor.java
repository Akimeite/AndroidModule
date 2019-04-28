package com.djangoogle.framework.retrofit;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

/**
 * Created by Djangoogle on 2019/04/28 11:48 with Android Studio.
 */
public class HttpLoggingInterceptor implements Interceptor {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private volatile Level printLevel = Level.NONE;
	private java.util.logging.Level colorLevel;
	private Logger logger;

	public enum Level {
		NONE,       //不打印log
		BASIC,      //只打印 请求首行 和 响应首行
		HEADERS,    //打印请求和响应的所有 Header
		BODY        //所有数据全部打印
	}

	public HttpLoggingInterceptor(String tag) {
		logger = Logger.getLogger(tag);
	}

	public void setPrintLevel(Level level) {
		if (printLevel == null) { throw new NullPointerException("printLevel == null. Use Level.NONE instead."); }
		printLevel = level;
	}

	public void setColorLevel(java.util.logging.Level level) {
		colorLevel = level;
	}

	private void log(String message) {
		logger.log(colorLevel, message);
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		if (printLevel == Level.NONE) {
			return chain.proceed(request);
		}

		//请求日志拦截
		logForRequest(request, chain.connection());

		//执行请求，计算请求时间
		long startNs = System.nanoTime();
		Response response;
		try {
			response = chain.proceed(request);
		} catch (Exception e) {
			log("<-- HTTP FAILED: " + e);
			throw e;
		}
		long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

		//响应日志拦截
		return logForResponse(response, tookMs);
	}

	private void logForRequest(Request request, Connection connection) throws IOException {
		boolean logBody = (printLevel == Level.BODY);
		boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
		RequestBody requestBody = request.body();
		boolean hasRequestBody = requestBody != null;
		Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

		try {
			String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
			log(requestStartMessage);

			if (logHeaders) {
				if (hasRequestBody) {
					// Request body headers are only present when installed as a network interceptor. Force
					// them to be included (when available) so there values are known.
					if (requestBody.contentType() != null) {
						log("\tContent-Type: " + requestBody.contentType());
					}
					if (requestBody.contentLength() != -1) {
						log("\tContent-Length: " + requestBody.contentLength());
					}
				}
				Headers headers = request.headers();
				for (int i = 0, count = headers.size(); i < count; i++) {
					String name = headers.name(i);
					// Skip headers from the request body as they are explicitly logged above.
					if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
						log("\t" + name + ": " + headers.value(i));
					}
				}

				log(" ");
				if (logBody && hasRequestBody) {
					if (isPlaintext(requestBody.contentType())) {
						bodyToString(request);
					} else {
						log("\tbody: maybe [binary body], omitted!");
					}
				}
			}
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			log("--> END " + request.method());
		}
	}

	private Response logForResponse(Response response, long tookMs) {
		Response.Builder builder = response.newBuilder();
		Response clone = builder.build();
		ResponseBody responseBody = clone.body();
		boolean logBody = (printLevel == Level.BODY);
		boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);

		try {
			log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
			if (logHeaders) {
				Headers headers = clone.headers();
				for (int i = 0, count = headers.size(); i < count; i++) {
					log("\t" + headers.name(i) + ": " + headers.value(i));
				}
				log(" ");
				if (logBody && HttpHeaders.hasBody(clone)) {
					if (responseBody == null) { return response; }

					if (isPlaintext(responseBody.contentType())) {
						byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
						MediaType contentType = responseBody.contentType();
						String body = new String(bytes, getCharset(contentType));
						log("\tbody:" + body);
						responseBody = ResponseBody.create(responseBody.contentType(), bytes);
						return response.newBuilder().body(responseBody).build();
					} else {
						log("\tbody: maybe [binary body], omitted!");
					}
				}
			}
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			log("<-- END HTTP");
		}
		return response;
	}

	private static Charset getCharset(MediaType contentType) {
		Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
		if (charset == null) { charset = UTF8; }
		return charset;
	}

	/**
	 * Returns true if the body in question probably contains human readable text. Uses a small sample
	 * of code points to detect unicode control characters commonly used in binary file signatures.
	 */
	private static boolean isPlaintext(MediaType mediaType) {
		if (mediaType == null) { return false; }
		if (mediaType.type() != null && mediaType.type().equals("text")) {
			return true;
		}
		String subtype = mediaType.subtype();
		if (subtype != null) {
			subtype = subtype.toLowerCase();
			//
			return subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains(
					"html");
		}
		return false;
	}

	private void bodyToString(Request request) {
		try {
			Request copy = request.newBuilder().build();
			RequestBody body = copy.body();
			if (body == null) { return; }
			Buffer buffer = new Buffer();
			body.writeTo(buffer);
			Charset charset = getCharset(body.contentType());
			log("\tbody:" + buffer.readString(charset));
		} catch (Exception e) {
			LogUtils.e(e);
		}
	}
}
