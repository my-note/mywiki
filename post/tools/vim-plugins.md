# vim-plugins




[插件管理](https://vimawesome.com/) 



## telescope

* 作用

    * 是一个高度可扩展的模糊查找器




* Keys Shortcuts

    * <leader>ff: find files
    * <leader>fg: grep string
    * <leader>fb: buffers
    * <leader>fh: tags
    * <leader>sf: file browers
    * <leader>/: current buffer 模糊搜索

* 依赖

    * ripgrep: `sudo apt-get install ripgrep`
    * fzf: `sudo apt-get install fzf`


## ack

* 作用

    * 在指定目录（默认当前目录）搜索指定字符串

* 依赖

    * `sudo apt-get install ack`


* Usage

    * `:Ack [options] {pattern} [{directories}]`

* Keyboard Shortcuts

    ```
    ?    a quick summary of these keys, repeat to close
    o    to open (same as Enter)
    O    to open and close the quickfix window
    go   to preview file, open but maintain focus on ack.vim results
    t    to open in new tab
    T    to open in new tab without moving to it
    h    to open in horizontal split
    H    to open in horizontal split, keeping focus on the results
    v    to open in vertical split
    gv   to open in vertical split, keeping focus on the results
    q    to close the quickfix window
    ```


























