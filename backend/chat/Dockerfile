# specify the node base image with your desired version node:<version>
FROM node:12-alpine

WORKDIR "/application"

# replace this with your application's default port
EXPOSE 3000

COPY package.json package.json
COPY index.js index.js
COPY index.html index.html
COPY start.sh start.sh

RUN mkdir log

RUN npm -y install \
    && chmod 777 ./start.sh

CMD ./start.sh