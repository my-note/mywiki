# git提交规范

**格式：**

```

<type>[optional scope]: <subject>
# 空行
[optional body]
# 空行
[optional footer]


```

**type:** 

|值|含义|
|-----|-----|
|feat|新功能新需求|
|fix|bug fix ,一次提交|
|to|bug fix, 多次提交，最终提交用fix|
|revert|撤销某修改|
|docs|文档|
|merge|代码合并|
|style|代码风格，格式化等|
|sync|主线同步（分支）|
|refactor|重构|
|test|test code|
|chore|构建过程或者辅助工具的变动，不修改源代码|
|perf|性能提升优化|
|improvement|对当前功能改进|
|build|构建工具修改|
|ci|持续集成的配置文件或脚本|


**scope：**

- 可选
- 模块名称
- 代码层名称（数据层， 服务层，视图层）

**subject:** 

- 必须
- 简短描述， 建议中文

**body:** 

- 可选
- markdown列表


**footer:** 

- 可选
- BREAK CHANGE:
- ISSUE:
- Fixes:
- Closes:
- Issues:




![](img/6.png)





















