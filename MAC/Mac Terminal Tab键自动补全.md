Mac Terminal Tab键自动补全

打开终端
nano .inputrc

在打开的文件中输入
set completion-ignore-case on
set show-all-if-ambiguous on
TAB:menu-complete

输入完
Control ＋ o
保存重新打开