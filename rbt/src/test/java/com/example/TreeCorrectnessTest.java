package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TreeCorrectnessTest {

    private RedBlackTree rbt;
    private SimpleBST bst;

    @BeforeEach
    void setUp() {
        rbt = new RedBlackTree();
        bst = new SimpleBST();
    }

    @Test
    @DisplayName("Functionality: Basic Insert, Contains, and Size")
    void testBasicOps() {
        int[] data = {50, 25, 75, 10, 30};
        for (int val : data) {
            assertTrue(rbt.insert(val));
            assertTrue(bst.insert(val));
        }
        
        assertEquals(5, rbt.size());
        assertTrue(rbt.contains(30));
        assertFalse(rbt.contains(99));
        assertFalse(rbt.insert(50), "Should not allow duplicates");
    }

    @Test
    @DisplayName("Functionality: Deletion cases")
    void testDeletion() {
        int[] data = {10, 20, 30, 40, 50};
        for (int val : data) rbt.insert(val);

        assertTrue(rbt.delete(30));
        assertFalse(rbt.contains(30));
        assertEquals(4, rbt.size());
    }

    @Test
    @DisplayName("Structural: Red-Black Tree Invariants")
    void testRBTInvariants() {
        for (int i = 1; i <= 100; i++) {
            rbt.insert(i);
        }

        double maxHeight = 2 * (Math.log(101) / Math.log(2));
        assertTrue(rbt.height() <= maxHeight, "Tree is not balanced!");

        assertDoesNotThrow(() -> RedBlackTree.Validator.check(rbt), 
            "RBT Invariant violation detected!");
    }

    @Test
    @DisplayName("Functionality: In-Order Traversal (Sorting)")
    void testSorting() {
        int[] data = {40, 10, 50, 20, 30};
        int[] expected = {10, 20, 30, 40, 50};
        
        for (int val : data) rbt.insert(val);
        
        assertArrayEquals(expected, rbt.inOrder(), "In-order traversal failed to sort");
    }
}
