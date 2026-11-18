class Arrays {
    public static void main(String[] a) {
        Xinu.printint(new Worker().run(5));
    }
}

class Worker {
    public int run(int n) {
        int[] nums;
        int total;
        nums = new int[n];
        nums[0] = 7;
        total = nums[1] + nums.length;
        return total;
    }
}