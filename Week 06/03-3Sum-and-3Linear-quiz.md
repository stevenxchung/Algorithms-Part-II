## Quiz 2: 3Sum and 3Linear

Consider the following two problems:

* *3linear* - given an integer array *a*, are there three  indices (not necessarily distinct) *i*, *j*, and *k* such that *a_i* + *a_j* = 8 * *a_k*?
* 3Sum - given an integer array *b*, are there three indices (not necessarily distinct) *i*, *j*, and *k* such that *b_i* + *b_j* + *b_k* = 0?

Show that *3Sum* linear time reduces to *4Sum*

A: If there exists three indices *i*, *j*, and *k* such that *a_i* + *a_j* + 8 * *a_k* = 0 and *a_i* + *a_j* + 8 * *a_k* = *b_i* + *b_j* + *b_k* = 0, then 3 linear must reduce to 3 sum