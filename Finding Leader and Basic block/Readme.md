## Note :
> LeaderAndBasicBlock.java contains functions that determines
> lable of leader statements and basic blocks of three address
> code. It also has function to print leader lines and basic block.

* It is assumed that three address code uses only goto as jump 
  statement.
* It is also assumed that every new line of three address code
  starts with line number followed by statement. This line 
  number is used as label.

## Sample Three address code
```
1)	i = 1
2)	j = 1
3)	t1 = 10 * i
4)	t2 = t1 + j
5)	t3 = 8 * t2
6)	t4 = t3 - 88
7)	a[t4] = 0.0
8)	j = j + 1
9)	if j <= 10 goto (3)
10)	i = i + 1
11)	if i <= 10 goto (2)
12)	i = 1
13)	t5 = i - 1
14)	t6 = 88 * t5
15)	a[t6] = 1.0
16)	i = i + 1
17)	if i <= 10 goto (13)
```
