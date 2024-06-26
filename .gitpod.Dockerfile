FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 21.0.1-tem && \
    sdk default java 21.0.1-tem && \
    yes | sdk install quarkus && \
    rm -rf $HOME/.sdkman/archives/* && \
    rm -rf $HOME/.sdkman/tmp/* "