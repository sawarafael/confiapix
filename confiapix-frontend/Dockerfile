FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
RUN apk add --no-cache gettext
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY --from=build /app/dist/confiapix-frontend/browser /usr/share/nginx/html
RUN chmod +x /docker-entrypoint.sh \
    && rm -f /etc/nginx/conf.d/default.conf
EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
