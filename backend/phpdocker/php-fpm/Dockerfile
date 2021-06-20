FROM phpdockerio/php80-fpm:latest
WORKDIR "/application"

# Fix debconf warnings upon build
ARG DEBIAN_FRONTEND=noninteractive

# Install selected extensions and other stuff
RUN  apt-get update
RUN  apt-get install -y curl
RUN  curl -sS https://getcomposer.org/installer -o composer-setup.php
RUN  php composer-setup.php --install-dir=/usr/local/bin --filename=composer
RUN  apt-get -y --no-install-recommends install git wget php-memcached php-xdebug php-yaml
RUN  apt-get -y --no-install-recommends install php-mysql
RUN  apt-get -y --no-install-recommends install php-bcmath php-mbstring php-gmp php-gd php-intl

