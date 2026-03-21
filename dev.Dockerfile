FROM storytellerf/android-in-docker:latest-dev

ARG USER_NAME

USER root

# 如果需要中文输入法
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y fcitx fcitx-googlepinyin

# 如果需要在容器中访问docker 的话
# RUN groupadd -g 1001 docker \
#     && usermod -aG docker $USER_NAME

USER $USER_NAME