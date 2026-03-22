FROM storytellerf/android-in-docker:latest-dev

ARG USER_NAME

USER root

# 如果需要中文输入法
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y fcitx fcitx-googlepinyin

USER $USER_NAME
WORKDIR /home/$USER_NAME

COPY --chown=$USER_NAME:$USER_NAME ./custom-entrypoint.sh ./bin/custom-entrypoint.sh
RUN chmod +x ./bin/custom-entrypoint.sh

ENTRYPOINT ["sh", "-c", "$HOME/bin/custom-entrypoint.sh"]
