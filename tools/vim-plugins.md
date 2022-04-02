# vim-plugins




[插件管理](https://vimawesome.com/) 



## jlanzarotta/bufexplorer

浏览缓存区文件

* <Leader>be normal open
* <Leader>bt toggle open / close
* <Leader>bs force horizontal split open
* <Leader>bv force vertical split open




## vim-visual-multi



1. 选中单词`<C-n>`, 继续按继续选中下一个
2. 垂直光标`<C-Down>/<C-Up>`
3. n/N get下一个/上一个发生处
4. q 跳过当前，到下一个发生处
5. Q 移除当前的光标和选中
6. 进入插入模式使用`i`,`a`, `I`,`A`

* 两个主要的模式：
    * 光标模式如果normal模式
    * 选中单词的模式如同可视模式




## 快速选中并进入visual模式

1. `vim-expand-region`: 使用`+`和`_`
    * 自定义成`=`和`-`
2. `gcmt/wildfire.vim`: 使用`<cr>` 和 `<bs>`
    * 自定义成`<s-cr>`和`<s-bs>`
3. `rhysd/vim-textobj-anyblock`
    - ib is a union of i(, i{, i[, i', i" and i<
    - ab is a union of a(, a{, a[, a', a" and a<

> 和vimwiki有冲突

## unimpaired

* `[q` => `:cprevious`
* `]a` => `:next`
* `[b` => `:bprevious`
* `[<Space>` => `在当前行的前面添加新行`
* `]<Space>` => `在当前行的后面添加新行`



## 对齐

### tabular


* 使用

    * 指定对齐`:Tab /=`: 通过`=`对齐
    * 冒号对齐`:Tab /:`: 通过`:`对齐, 加强`:Tab /:\zs`

* keys Shortcuts

    ```
    nmap <Leader>a= :Tabularize /=<CR>
    vmap <Leader>a= :Tabularize /=<CR>
    nmap <Leader>a: :Tabularize /:\zs<CR>
    vmap <Leader>a: :Tabularize /:\zs<CR>
    ```

### vim-easy-align

* Keys Shortcuts

    ```
    " Start interactive EasyAlign in visual mode (e.g. vipga)
    xmap ga <Plug>(EasyAlign)

    " Start interactive EasyAlign for a motion/text object (e.g. gaip)
    nmap ga <Plug>(EasyAlign)
    ```

* e.g.

    * `vipga=`: 可视模式执行

        - visual-select inner paragraph
        - Start EasyAlign command (ga)
        - Align around =

    * `gaip=`: 正常模式执行，`<operator><motion>`






## telescope

* 作用

    * 是一个高度可扩展的模糊查找器




* Keys Shortcuts

    * `<leader>ff`: find files
    * `<leader>fg`: grep string
    * `<leader>fb`: buffers
    * `<leader>fh`: tags
    * `<leader>sf`: file browers
    * `<leader>/`: current buffer 模糊搜索

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


























