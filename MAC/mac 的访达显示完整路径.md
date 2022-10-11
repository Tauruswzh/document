mac 的访达显示完整路径

1.在 终端中输入
defaults write com.apple.finder _FXShowPosixPathInTitle -bool TRUE;killall Finder

2.取消显示
defaults delete com.apple.finder _FXShowPosixPathInTitle;killall Finder