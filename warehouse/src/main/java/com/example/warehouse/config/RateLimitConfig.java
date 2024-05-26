package com.example.warehouse.config;

// import com.example.warehouse.interceptor.RateLimitInterceptor;
// import io.github.bucket4j.Bandwidth;
// import io.github.bucket4j.Bucket;
// import io.github.bucket4j.Bucket4j;
// import io.github.bucket4j.Refill;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.*;

// import java.time.Duration;


// @Configuration
// public class RateLimitConfig implements WebMvcConfigurer {

// 	@Override
// 	  public void addInterceptors(InterceptorRegistry registry) {
// 	    Refill refill = Refill.greedy(30, Duration.ofMinutes(1));
// 	    Bandwidth limit = Bandwidth.classic(30, refill).withInitialTokens(1);
// 	    Bucket bucket = Bucket4j.builder().addLimit(limit).build();
// 	    registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns("/items");

// 	    refill = Refill.intervally(20, Duration.ofMinutes(1));
// 	    limit = Bandwidth.classic(20, refill);
// 	    bucket = Bucket4j.builder().addLimit(limit).build();
// 	    registry.addInterceptor(new RateLimitInterceptor(bucket, 1))
// 	        .addPathPatterns("/items/**");
// 	  }
// }

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    public static Bucket createBucket() {
        Refill refill = Refill.greedy(50, Duration.ofMinutes(1)); // 每分钟添加50个令牌
        Bandwidth limit = Bandwidth.classic(50, refill); // 桶的容量为50个令牌

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
