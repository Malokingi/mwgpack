from ast import List
import re
from typing import Optional

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next
class TreeNode:
    def __init__(self, x):
        self.val = x
        self.left = None
        self.right = None

class two_sum:
    # List
    def twoSum(self, ns: List[int], t: int) -> List[int]:
        l = list()
        for i in ns:
            if t - i in l: return {l.index(t - i), len(l)}
            l.append(i)
        return []
    # Inverse Dictionary
    def twoSum(self, ns: List[int], t: int) -> List[int]:
        d = {}
        for i in range(len(ns)):
            reqNum = t - ns[i]
            if reqNum in d:
                return [d[reqNum], i]
            d[ns[i]] = i
        return []

class valid_parentheses:
    # Stack (made out of a List)
    def isValid(self, s: str) -> bool:
        stack = []
        for c in list(s):
            if "({[".find(c) != -1 or len(stack) == 0:
                stack.append(c)
            elif ")}]".find(c) != "({[".find(stack.pop()):
                return False
        return not stack

class merge_two_sorted_lists:
    # General Single Linked List Manipulation
    def mergeTwoLists(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:
        if not (list1 or list2):
            return list1 if list1 else list2
        tmpNode = head = ListNode()
        while list1 and list2:
            if list1.val < list2.val:
                tmpNode.next = tmpNode = list1
                list1 = list1.next
            else:
                tmpNode.next = tmpNode = list2
                list2 = list2.next
        if list1 or list2:
            tmpNode.next = list1 if list1 else list2
        return head.next

class best_time_to_buy_and_sell_stock:
    # List Analysis
    def maxProfit(self, prices: List[int]) -> int:
        tmpMin, tmpMax = 10_000, 0
        for p in prices:
            tmpMin = min(tmpMin, p) # if this is a new low, reset the lower limit
            tmpMax = max(p - tmpMin, tmpMax) # if todays price is the greatest difference from the lower limit so far, reset the higher limit
        return tmpMax

class valid_palindrome:
    # With head and tail pointers
    def isPalindrome(self, s: str) -> bool:
        s, head, tail = s.lower(), 0, len(s) - 1
        while head < tail:
            # Find next valid left char
            while not (s[head].isalpha() or s[head].isdigit()):
                if head >= tail:
                    return True
                else:
                    head += 1
            # Find next valid right char
            while not (s[tail].isalpha() or s[tail].isdigit()):
                if head >= tail:
                    return True
                else:
                    tail -= 1
            if s[head] != s[tail]:
                return False
            head, tail = head + 1, tail - 1
        return True
    # With regex
    def isPalindrome(self, s: str) -> bool:
        # Edit sring to remove all non-digit, non-letter chars and converts to lowercase
        s = re.sub(r"[^a-zA-Z0-9]", "", s).lower()
        # s[::-1] returns the reverse of s b/c [::-1] says to iterate backwards starting from one space before the beginning (aka the end)
        return s == s[::-1]

class binary_search:
    # Basic Binary Search on a List
    def search(self, nums: List[int], target: int) -> int:
        l_idx, r_idx = 0, len(nums) - 1

        while l_idx <= r_idx:
            m_idx = int((l_idx + r_idx) / 2) # Rounds down
            m_num = nums[m_idx]
            if m_num == target:
                return m_idx
            if m_num > target:
                r_idx = m_idx - 1
            else:
                l_idx = m_idx + 1
        return -1

class flood_fill:
    # Recursive
    def floodFill(self, image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:
        if image[sr][sc] == color: return image
        doomed_color = image[sr][sc]
        image[sr][sc] = color

        if sr > 0 and image[sr - 1][sc] == doomed_color: # Checking Up
            image = self.floodFill(image, sr - 1, sc, color)
        if sc > 0 and image[sr][sc - 1] == doomed_color: # Checking Left
            image = self.floodFill(image, sr, sc - 1, color)
        if sr < len(image) - 1 and image[sr + 1][sc] == doomed_color: # Checking Down
            image = self.floodFill(image, sr + 1, sc, color)
        if sc < len(image[0]) - 1 and image[sr][sc + 1] == doomed_color: # Checking Right
            image = self.floodFill(image, sr, sc + 1, color)
        return image
    # Resursive still but with a function
    def floodFill(self, image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:
        doomed_color = image[sr][sc]
        r_range = range(0, len(image))
        c_range = range(0, len(image[0]))

        def fill(r, c):
            if (
                not r in r_range or 
                not c in c_range or 
                image[r][c] == color or 
                image[r][c] != doomed_color
            ): return
            
            image[r][c] = color
            fill(r - 1, c)
            fill(r, c - 1)
            fill(r + 1, c)
            fill(r, c + 1)
        fill(sr, sc)
        return image

class lca_of_binary_tree:
    # With a while loop
    def lowestCommonAncestor(self, root: 'TreeNode', p: 'TreeNode', q: 'TreeNode') -> 'TreeNode':
        a = min(p.val, q.val)
        b = max(p.val, q.val)
        target_range = range(a, b + 1)
        while root:
            # if the current value is between p and q (inclusive), we have the answer
            if root.val in target_range: return root
            if root.val > b: root = root.left
            if root.val < a: root = root.right
        return root
    # With a recursive function
    def lowestCommonAncestor(self, root: 'TreeNode', p: 'TreeNode', q: 'TreeNode') -> 'TreeNode':
        pv, qv = p.val, q.val
        def LCA(r):
            rv = r.val
            if pv < rv and qv < rv: return LCA(r.left)
            if pv > rv and qv > rv: return LCA(r.right)
            return r
        return LCA(root)

