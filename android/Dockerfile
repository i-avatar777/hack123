FROM openjdk:8-jdk

# Just matched `app/build.gradle`
#ENV ANDROID_COMPILE_SDK "29"
ENV ANDROID_COMPILE_SDK "30"
# Just matched `app/build.gradle`
#ENV ANDROID_BUILD_TOOLS "29.0.2"
ENV ANDROID_BUILD_TOOLS "30.0.3"
# Version from https://developer.android.com/studio/releases/sdk-tools
# SDK_TOOLS deprecated. Use cmdline-tools instead.
ENV ANDROID_SDK_TOOLS "6858069"

ENV ANDROID_HOME /android-sdk-linux
ENV PATH="${PATH}:/android-sdk-linux/platform-tools/"

# install OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1 build-essential ruby ruby-dev
# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common
# install Android SDK
ADD https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip sdk-tools-linux.zip
RUN unzip sdk-tools-linux.zip -d ${ANDROID_HOME} && mkdir ${ANDROID_HOME}/sdk-latest && \
    mv ${ANDROID_HOME}/cmdline-tools/* ${ANDROID_HOME}/sdk-latest/ && \
    mv ${ANDROID_HOME}/sdk-latest ${ANDROID_HOME}/cmdline-tools/latest && \
    rm sdk-tools-linux.zip && \
    echo y | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager "platforms;android-${ANDROID_COMPILE_SDK}" "build-tools;${ANDROID_BUILD_TOOLS}"

# install Fastlane
COPY Gemfile.lock .
COPY Gemfile .
RUN mkdir fastlane
COPY fastlane/Pluginfile  fastlane/
RUN gem install bundle &&  bundle install && bundle exec fastlane add_plugin fastlane-plugin-firebase
