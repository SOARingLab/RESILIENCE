FROM nginx
COPY dist/dsl-frontend /usr/share/nginx/html
COPY default.conf /etc/nginx/conf.d/default.conf
CMD ["nginx","-g","daemon off;"]
