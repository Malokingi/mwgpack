import java.util.*;

class nodes {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
            next = null;
        }
    }
}

class others {
    public int romanToInt(String s) {
        int retTotal = 0, curInt = 0, prevInt = 1000;
        final Map<Character, Integer> riMap = new HashMap<>(){{
            put('I', 1);
            put('V', 5);
            put('X', 10);
            put('L', 50);
            put('C', 100);
            put('D', 500);
            put('M', 1000);
        }};
        for(char c : s.toCharArray()){
            curInt = riMap.get(c);
            if(curInt > prevInt)
                retTotal -= (2 * prevInt);
            retTotal += curInt;
            prevInt = curInt;
        }
        return retTotal;
    }

    public static int reverseDigits(int n) {
        int ret = 0;
        while (n > 0) {
            ret = ret * 10 + n % 10;
            n /= 10;
        }
        return ret;
    }

    public int maxProfit(int[] prices) {
        short min = 10000, max = 0;
        for(int price: prices) {
            min = (short) Math.min(min, price);
            max = (short) Math.max(price - min, max);
        }
        return max;
    }

    public boolean isPalindrome(String s) {
        char[] cArray = s.toLowerCase().toCharArray();
        int head = -1, tail = cArray.length;
        l: while(++head < --tail) {
            while(!Character.isLetterOrDigit(cArray[head])) if(++head > tail) break l;
            while(!Character.isLetterOrDigit(cArray[tail])) if(--tail < head) break l;
            if(cArray[head] != cArray[tail]) return false;
        }
        return true;
    }

    public boolean isAnagram(String s, String t) {
        short[] a = new short[26];
        for(char c : s.toCharArray()) a[c-'a']++;
        for(char c : t.toCharArray()) a[c-'a']--;
        for(short i : a) if(i != 0) return false;
        return true;
    }

    // Fibonacci
    private int recursiveFib(int n) {
        if(n < 2) return 1;
        return recursiveFib(n - 1) + recursiveFib(n - 2);
    }
    HashMap<Integer, Integer> fibMemo = new HashMap<>();
    private int memoizedFib(int n) {
        if(n < 2) return 1;
        if(fibMemo.containsKey(n)) return fibMemo.get(n);
        fibMemo.put(n, memoizedFib(n - 1) + memoizedFib(n - 2));
        return fibMemo.get(n);
    }

    public int countCommonFactors(int a, int b) {
        byte r = 0;
        short ab = (short) a, bb = (short) b;
        while(!(ab == 0 || bb == 0)) {
            if(ab < bb) {
                bb -= ab;
                continue;
            }
            ab -= bb;
        }
        for(short i = 1; i <= ab + bb; i++)
            if(a % i == 0 && b % i == 0) r++;
        return r;
    }

    public int boyerMooreVotingAlgorithm(int[] nums) {
        int can = 0, count = 0; // current candidate, count
        for(int n : nums) {
            if(count < 1) {
                can = n;
                count = 1;
                continue;
            }
            count += (n == can) ? 1 : -1;
        }
        return can;
    }

    public String addBinary(String a, String b) {
        if(a.equals("0")) return b;
        if(b.equals("0")) return a;
        StringBuilder r = new StringBuilder();
        boolean c = false;
        
        if(a.length() > b.length()) b = makeSameLength(b, a.length());
        else if(a.length() < b.length()) a = makeSameLength(a, b.length());
        
        for(int i = a.length() - 1; i > -1; i--){
            byte ones = (byte)
                        (((a.charAt(i) == '1') ? 1 : 0) +
                        ( (b.charAt(i) == '1') ? 1 : 0) +
                        ( (c) ? 1 : 0));
            r.append((ones % 2 == 0) ? '0' : '1');
            c = (ones > 1) ? true : false;
        }
        return (c) ? r.append('1').reverse().toString() : r.reverse().toString();
    }
    private String makeSameLength(String s, int l) {
        l -= s.length();
        StringBuilder r = new StringBuilder(s).reverse();
        while(l-- > 0) r.append('0');
        return r.reverse().toString();
    }
}

class twoDArrays {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        if(image[sr][sc] == color) return image;
        int doomed = image[sr][sc];
        image[sr][sc] = color;
        if(sr > 0 && image[sr - 1][sc] == doomed) // up
            image = floodFill(image, sr - 1, sc, color);
        if(sr < image.length - 1 && image[sr + 1][sc] == doomed) // down
            image = floodFill(image, sr + 1, sc, color);
        if(sc > 0 && image[sr][sc - 1] == doomed) // left
            image = floodFill(image, sr, sc - 1, color);
        if(sc < image[0].length - 1 && image[sr][sc + 1] == doomed) // right
            image = floodFill(image, sr, sc + 1, color);
        return image;
    }

    public int maxHourglassSum(int[][] g) {
        int r = 0;
        for(short i = 0; i < g.length - 2; i++)
            for(short j = 0; j < g[0].length - 2; j++)
                r = Math.max(r, 
                    g[ i ][ j ] + g[ i ][j+1] + g[ i ][j+2] +
                                  g[i+1][j+1] +
                    g[i+2][ j ] + g[i+2][j+1] + g[i+2][j+2]);
        return r;
    }

    public int hardestWorker(int n, int[][] logs) {
        int m = logs[0][1], id = logs[0][0];
        for(int i = 1; i < logs.length; i++) {
            int l = logs[i][1] - logs[i-1][1];
            if(l > m || (l == m && id > logs[i][0])){
                m = l;
                id = logs[i][0];
            }
        }
        return id;
    }

    public int uniquePaths(int m, int n) {
        int[][] grid = new int[m][n];
        for(int i = 0; i<m; i++){
            for(int j = 0; j<n; j++){
                if(i==0||j==0) grid[i][j] = 1;
                else grid[i][j] = grid[i][j-1] + grid[i-1][j];
            }
        }
        return grid[m-1][n-1];
    }

    private void printGrid(int[][] g) {
        String s1 = "", s2 = "";
        int n = (int) (Math.floor(Math.log10(g[g.length-1][g[0].length-1])) + 1);
        for(int i = 0; i < g.length; i++) {
            s1 = "";
            s2 = "";
            int di = (int) (Math.floor(Math.log10(g[i][0])) + 1);
            for(int k = di; k < n; k++) {
                if(s1.length() <= s2.length()) s1 += " ";
                else s2 += " ";
            }
            System.out.printf("[%s%d%s", s1, g[i][0], s2);
            for(int j = 1; j < g[0].length; j++) {
                s1 = "";
                s2 = "";
                int dj = (int) (Math.floor(Math.log10(g[i][j])) + 1);
                // System.out.printf("\n n = %d, di = %d, dj = %d", n, di, dj);
                for(int k = dj; k < n; k++) {
                    if(s1.length() <= s2.length()) s1 += " ";
                    else s2 += " ";
                }
                System.out.printf("][%s%d%s", s1, g[i][j], s2);
            }
            System.out.println("]");
        }
        System.out.println();
    }
}

class singleLists {
    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
            next = null;
        }
    }
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        if(list1 == null || list2 == null) return (list2 == null) ? list1 : list2;
        ListNode head;
        ListNode sln; //transient singly linked list node
        if(list1.val < list2.val){
            sln = new ListNode(list1.val);
            list1 = list1.next;
        }
        else{
            sln = new ListNode(list2.val);
            list2 = list2.next;
        }
        head = sln;
        while(!(list1 == null && list2 == null)){
            if(list2 != null && (list1 == null || list1.val > list2.val)){
                sln.next = new ListNode(list2.val);
                list2 = list2.next;
            }
            else {
                sln.next = new ListNode(list1.val);
                list1 = list1.next;
            }
            sln = sln.next;
        }
        return head;
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head, sln = new ListNode(l1.val + l2.val);
        boolean c = sln.val > 9; // carried 1
        l1 = l1.next;
        l2 = l2.next;
        head = sln;
        while(l1 != null || l2 != null || c) {
            if(c) sln.val -= 10;
            sln.next = new ListNode(
                (c ? 1 : 0) + 
                (l1 != null ? l1.val : 0) + 
                (l2 != null ? l2.val : 0)
            );
            if(l1 != null) l1 = l1.next;
            if(l2 != null) l2 = l2.next;
            sln = sln.next;
            c = sln.val > 9;
        }
        return head;
    }

    public ListNode middleNode(ListNode head) { // "middle" rounded up
        ListNode fast = head;
        while(fast.next != null) {
            if(fast.next.next == null) return head.next;
            head = head.next;
            fast = fast.next.next;
        }
        return head;
    }
}

class binaryTrees{
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    /*
     * Calculate height
     */
    public int recursiveTreeHeight(TreeNode r) {
        if(r == null) return 0;
        int lh = recursiveTreeHeight(r.left), rh = recursiveTreeHeight(r.right);
        return Math.max(lh, rh) + 1;
    }
    HashMap<TreeNode, Integer> h = new HashMap<>(); // heights
    public int memoizedTreeHeight(TreeNode r) {
        if(r == null) return 0;
        if(!h.containsKey(r)) {
            h.put(r, Math.max(memoizedTreeHeight(r.left), memoizedTreeHeight(r.right)) + 1);
        }
        return h.get(r);
    }
    public int iteritiveTreeHeight(TreeNode r) {
        if(r == null) return 0;
        
        int dep = 0; // th
        Queue<TreeNode> q = new LinkedList<>();
        q.add(r);
        while(!q.isEmpty()) {
            dep++;
            int s = q.size();
            for(int i = 0; i < s; i++) { // clear out layer and add the next layer
                TreeNode tmp = q.poll();
                if(tmp.left != null) q.add(tmp.left);
                if(tmp.right != null) q.add(tmp.right);
            }
        }
        return dep;
    }

    private int h(TreeNode r) { return (r == null) ? 0 : Math.max(h(r.left) + 1, h(r.right) + 1); }

    /*
     * Calculate max "diamater" - max distamce between any two nodes.
     * (or max number of edges, graph-wise)
     */
    int max = 0;
    public int diameterOfBinaryTree(TreeNode r) {
        calcTreeHeights(r);
        return max;
    }
    
    private int calcTreeHeights(TreeNode r) {
        if(r == null || r.left == null && r.right == null) return 0;
        int lh = (r.left == null) ? 0 : calcTreeHeights(r.left) + 1,
            rh = (r.right == null) ? 0 : calcTreeHeights(r.right) + 1;
        
        max = Math.max(max, (lh + rh));
        
        return Math.max(lh, rh);
    }

    public void navigation(TreeNode root) {
        int v = root.val, lv = root.left.val, rv = root.right.val;
        // 
        while((v - lv) * (v - rv) > 0) {
            root = (lv < v) ? root.left : root.right; // e or d
            v = root.val;
        }
    }

    private void iteritivePrintBFS(TreeNode root) {
        if(root == null) return;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            TreeNode tmp = queue.poll();
            System.out.print(tmp.val + " ");
            if(tmp.left != null) queue.add(tmp.left);
            if(tmp.right != null) queue.add(tmp.right);
        }
    }

    public TreeNode recursiveInvertTree(TreeNode root) {
        if(root == null || (root.left == null && root.right == null)) return root;
        TreeNode rRoot = new TreeNode(root.val); // Root of the Returned tree.
        if(root.right != null) rRoot.left = recursiveInvertTree(root.right);
        if(root.left != null) rRoot.right = recursiveInvertTree(root.left);
        return rRoot;
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        int rv = root.val, pv = p.val, qv = q.val;
        if(pv < rv && qv < rv) return lowestCommonAncestor(root.left, p, q); // e
        if(pv > rv && qv > rv) return lowestCommonAncestor(root.right, p, q); // d
        return root; // b or c
    }
}

class hashTables {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Float, Integer> m = new HashMap<Float, Integer>();
        for(float i: nums){
            if(m.containsKey((target - i))) return new int[] {m.get(target - i), m.size()};
            while(m.containsKey(i)) i += 0.1;
            m.put(i, m.size());
        }
        return null;
    }

    public int findMaxValueWhoseComplimentExists(int[] nums) {
        short ret = -1; 
        // I'll just use a byte as a flag. 1 = pos found, 2 = neg found, 3 = both found
        HashMap<Short, Byte> t = new HashMap<>();
        for(int n : nums) {
            short i = (short) n;
            short k = (short) Math.abs(i);
            byte v;
            if(k < ret) continue; // These should save some time
            if(!t.containsKey(k)){ t.put(k, (byte) ((i > 1) ? 1 : 2)); continue; }
            if((v = t.get(k)) == 3) continue;
            
            if(i > 0) t.replace(k, (byte) ((v == 2) ? 3 : 1));
            else t.replace(k, (byte) ((v == 1) ? 3 : 2));
            if(t.get(k) == 3) ret = (short) Math.max(ret, k);
        }
        return (int) ret;
    }
}

class sets {
    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
            next = null;
        }
    }
    
    public int countDistinctIntegers(int[] nums) {
        Set<Integer> numCount = new HashSet<>();
        for(int i : nums) {
            numCount.add(i);
            numCount.add(others.reverseDigits(i));
        }
        return numCount.size();
    }
    public boolean hasCycle(ListNode head) {
        Set<ListNode> visited = new HashSet<>();
        while(!visited.contains(head)) {
            if(head == null || head.next == null) return false;
            visited.add(head);
            head = head.next;
        }
        return true;
    }
    // Just switch true and false to make this into allUnique()
    // Or better yet call it as !containsDuplicate()
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> has = new HashSet<>();
        for(int e : nums) if(!has.add(e)) return true;
        return false;
    }
}

class stacks {
    public String robotWithString(String s) {
        Stack<Character> t = new Stack<>();
        StringBuilder p = new StringBuilder();
        int[] lets = new int[26];
        
        for(int i = 0; i < s.length(); i++) lets[s.charAt(i)-'a']++; // counts Letters in s
        
        for(int i = 0; i < s.length(); i++) { // empties s
            char c = s.charAt(i);
            t.add(c); // Op1
            lets[c-'a']--;
            while(!t.isEmpty() && t.peek() <= minSChar(lets)) p.append(t.pop()); // Op2
        }
        
        while(!t.isEmpty()) p.append(t.pop()); // Op2 after no more Op1s are possible
        
        return p.toString();
    }
    
    private char minSChar(int[] lets) {
        for(int i = 0; i < 26; i++)
            if(lets[i] > 0) return (char) (i + 'a');
        return '{'; // (char) a - 1
    }

    public boolean isValidPara(String s) {
        Stack<Character> x = new Stack<>();
        for(char c: s.toCharArray()){
            if("({[".indexOf(c) != -1 || x.empty()) x.push(c);
            else if(")}]".indexOf(c) != "({[".indexOf(x.pop())) return false;
        }
        return x.empty();
    }
}

class queues {
    class stackQueue {
        Stack<Byte> i = new Stack<>(), o = new Stack<>(); // in, out
        
        public void push(int x) { i.add((byte) x); }
        
        public byte pop() { return peek(true); }
        
        public byte peek() { return peek(false); }
        
        public byte peek(boolean pop) { 
            unload();
            return (pop) ? o.pop() : o.peek();
        }
        
        private void unload() { if(o.isEmpty()) while(!i.isEmpty()) o.add(i.pop()); }
        
        public boolean empty() { return i.isEmpty() && o.isEmpty(); }
    }
}

class searchAlgorithms {
    public int recursiveBinarySearch(int[] nums, int target) { return recursiveBinarySearch(nums, target, 0, nums.length - 1); }
    private int recursiveBinarySearch(int[] nums, int target, int left, int right) {
        if(right - left < 0) return -1;
        int m = (right + left) / 2;
        if(nums[m] == target) return m;
        if(nums[m] > target) return recursiveBinarySearch(nums, target, left, (m - 1));
        return recursiveBinarySearch(nums, target, (m + 1), right);
    }

    public int iterativeBinarySearch(int[] nums, int target) {
        int l = 0, m = nums.length / 2, r = nums.length - 1;
        if(target < nums[l] || target > nums[r]) return -1;
        if(nums[m] == target) return m;
        while(r - l > -1){
            if(nums[m] == target) return m;
            if(nums[m] > target) r = m - 1;
            if(nums[m] < target) l = m + 1;
            m = (r + l) / 2;
        }
        return -1;
    }
}

class XOR_forSomeReason {
    public int minimizeXor(int num1, int num2) {
        int numBit1 = Integer.bitCount(num1), numBit2 = Integer.bitCount(num2);
        if(numBit1 == numBit2) return num1;
        int r = num1; // r for 'return value'
        int xSize = Math.max(
            (int) (Math.log(num1) / Math.log(2)), 
            (int) (Math.log(num2) / Math.log(2))
        );
        for(int i = 0; i <= xSize; ++i) {
            int curBit = 1 << i;
            int bitExtant = curBit & num1;
            if(numBit2 > numBit1 && bitExtant == 0) {
                r = r ^ curBit;
                numBit1++;
            }
            if(numBit2 < numBit1 && bitExtant != 0) {
                r = r ^ curBit;
                numBit1--;
            }
        }
        return r;
    }
}