#######################################################
# Multi-stage Dockerfile for MkDocs with Nginx
#
# Stage 1 : Build the MkDocs site using a Python image
# Stage 2 : Serve the built site using Nginx
#######################################################

# Stage 1 : builder
FROM python:3.11-alpine AS builder

WORKDIR /app

# mkdocs インストール
RUN pip install mkdocs

# プロジェクトコピー
COPY . .

# ビルド
RUN mkdocs build


# Stage 2 : runtime
FROM nginx:alpine

# 既存削除
RUN rm -rf /usr/share/nginx/html/*

# builder から成果物をコピー
COPY --from=builder /app/site /usr/share/nginx/html

# Nginx の設定ファイルをコピー
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]