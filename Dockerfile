#  Ý tưởng build docker tối ưu                Flow build
#    Multi-stage build                        Source code
#          +                                       ↓
#    Spring Boot layered jar                  Docker build
#          +                                       ↓
#    JRE Alpine                              Maven build jar
#                                                  ↓
#                                            extract layers
#                                                  ↓
#                                            runtime image nhẹ


# ---------- Stage 1: build app ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# copy pom trước để cache dependency
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# copy source
COPY src ./src

# build jar
RUN mvn -B -q -DskipTests package

# ---------- Stage 2: runtime ----------

# dùng java 21 runtime
FROM eclipse-temurin:21-jre-alpine AS extractor

# thư mục làm việc
WORKDIR /app

# copy jar từ stage build
COPY --from=builder /build/target/*.jar app.jar

# extract các layer của spring boot
RUN java -Djarmode=layertools -jar app.jar extract

# ---------- Stage 3: runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Ho_Chi_Minh"

# timezone
RUN ln -sf /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime

# copy từng layer để Docker cache
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/application/ ./

# expose port spring boot
EXPOSE 8080

# chạy spring boot bằng JarLauncher
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]