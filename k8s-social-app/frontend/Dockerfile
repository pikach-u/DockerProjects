# 멀티 스테이지 빌드: 빌드 스테이지
FROM node:20-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사 (Docker 레이어 캐싱 최적화)
COPY package*.json ./

# 의존성 설치
RUN npm install

# 소스 코드 복사
COPY . .

# 프로덕션 빌드 실행
RUN npm run build

# 프로덕션 스테이지: Nginx로 정적 파일 서빙
FROM nginx:alpine

# 빌드된 React 앱을 nginx 웹 루트로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# 커스텀 nginx 설정 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# nginx 기본 설정 제거
RUN rm /etc/nginx/conf.d/default.conf.bak 2>/dev/null || true

# 포트 노출
EXPOSE 80

# nginx 실행 (데몬 모드 비활성화)
CMD ["nginx", "-g", "daemon off;"]