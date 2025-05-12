package production;

import java.util.*;

class ChatBot {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChatManager chatManager = new ChatManager();

        String[] commands = {
                "/shortestpath",
                "/autocomplete",
                "/addtask",
                "/decisiontree",
                "/kmp",
                "/lcs",
                "/undo",
                "/history"
        };

        while (true) {
            System.out.print("\nYou: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("/quit")) {
                System.out.println("Goodbye!");
                break;
            } else if (userInput.equalsIgnoreCase("/help")) {
                System.out.println("Commands available:");
                System.out.println("1. /shortestpath - Find shortest path between nodes.");
                System.out.println("2. /autocomplete - Suggest commands based on input.");
                System.out.println("3. /addtask - Add prioritized tasks.");
                System.out.println("4. /decisiontree - Interact or modify decision tree.");
                System.out.println("5. /kmp - Perform KMP string search.");
                System.out.println("6. /lcs - Calculate the longest common subsequence of two strings.");
                System.out.println("7. /undo - Undo last action.");
                System.out.println("8. /history - View chat history.");
            } else if (userInput.equals("1")) {
                userInput = "/shortestpath";
            } else if (userInput.equals("2")) {
                userInput = "/autocomplete";
            } else if (userInput.equals("3")) {
                userInput = "/addtask";
            } else if (userInput.equals("4")) {
                userInput = "/decisiontree";
            } else if (userInput.equals("5")) {
                userInput = "/kmp";
            } else if (userInput.equals("6")) {
                userInput = "/lcs";
            } else if (userInput.equals("7")) {
                userInput = "/undo";
            } else if (userInput.equals("8")) {
                userInput = "/history";
            }

            if (userInput.equalsIgnoreCase("/autocomplete")) {
                System.out.print("Enter prefix: ");
                String prefix = scanner.nextLine();
                System.out.println("Suggestions: " + autocomplete(commands, prefix));
            } else if (userInput.equalsIgnoreCase("/addtask")) {
                System.out.print("Enter task description: ");
                String task = scanner.nextLine();
                System.out.print("Enter priority (lower is higher priority): ");
                int priority = Integer.parseInt(scanner.nextLine());
                chatManager.addTask(task, priority);
                System.out.println("Task added!");
            } else if (userInput.equalsIgnoreCase("/decisiontree")) {

            } else if (userInput.equalsIgnoreCase("/shortestpath")) {
                System.out.println("Enter edges in the format 'A-B:weight'. Type 'done' to finish.");
                Graph graph = new Graph();
                while (true) {
                    System.out.print("Edge: ");
                    String edge = scanner.nextLine();
                    if (edge.equalsIgnoreCase("done")) break;
                    String[] parts = edge.split(":");
                    String[] nodes = parts[0].split("-");
                    int weight = Integer.parseInt(parts[1]);
                    graph.addEdge(nodes[0], nodes[1], weight);
                }
                System.out.print("Source node: ");
                String source = scanner.nextLine();
                graph.dijkstra(source);
            } else if (userInput.equalsIgnoreCase("/kmp")) {
                System.out.print("Enter the text: ");
                String text = scanner.nextLine();
                System.out.print("Enter the pattern: ");
                String pattern = scanner.nextLine();
                KMP kmp = new KMP();
                List<Integer> matches = kmp.kmpSearch(text, pattern);
                System.out.println("Pattern found at indices: " + matches);
            } else if (userInput.equalsIgnoreCase("/lcs")) {
                System.out.print("Enter the first string: ");
                String str1 = scanner.nextLine();
                System.out.print("Enter the second string: ");
                String str2 = scanner.nextLine();
                LCS lcs = new LCS();
                String result = lcs.findLCS(str1, str2);
                System.out.println("Longest Common Subsequence: " + result);
            } else if (userInput.equalsIgnoreCase("/undo")) {
                chatManager.undoLastMessage();
            } else if (userInput.equalsIgnoreCase("/history")) {
                chatManager.printChatHistory();
            } else {
                String response = chatManager.handleChat(userInput);
                System.out.println("Bot: " + response);
            }
        }

        scanner.close();
    }

    public static List<String> autocomplete(String[] commands, String prefix) {
        List<String> suggestions = new ArrayList<>();
        for (String command : commands) {
            if (command.startsWith(prefix)) {
                suggestions.add(command);
            }
        }
        return suggestions;
    }
}

class ChatManager {
    private List<String> chatHistory = new ArrayList<>();
    private PriorityQueue<Task> taskQueue = new PriorityQueue<>(Comparator.comparingInt(task -> task.priority));

    public String handleChat(String message) {
        chatHistory.add(message);
        return "I'm here to help! Use /help to see options.";
    }

    public void printChatHistory() {
        System.out.println("Chat History:");
        for (String message : chatHistory) {
            System.out.println(message);
        }
    }

    public void undoLastMessage() {
        if (!chatHistory.isEmpty()) {
            chatHistory.remove(chatHistory.size() - 1);
            System.out.println("Last message undone.");
        } else {
            System.out.println("No messages to undo.");
        }
    }

    public void addTask(String description, int priority) {
        taskQueue.add(new Task(description, priority));
    }
}

class Task {
    String description;
    int priority;

    public Task(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }
}

class Graph {
    private Map<String, List<Edge>> adjList = new HashMap<>();

    public void addEdge(String src, String dest, int weight) {
        adjList.computeIfAbsent(src, k -> new ArrayList<>()).add(new Edge(dest, weight));
    }

    public void dijkstra(String source) {
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(edge -> edge.weight));

        pq.add(new Edge(source, 0));
        while (!pq.isEmpty()) {
            Edge current = pq.poll();

            if (!distances.containsKey(current.node)) {
                distances.put(current.node, current.weight);

                for (Edge neighbor : adjList.getOrDefault(current.node, new ArrayList<>())) {
                    if (!distances.containsKey(neighbor.node)) {
                        pq.add(new Edge(neighbor.node, current.weight + neighbor.weight));
                    }
                }
            }
        }

        System.out.println("Shortest distances from " + source + ": " + distances);
    }
}

class Edge {
    String node;
    int weight;

    public Edge(String node, int weight) {
        this.node = node;
        this.weight = weight;
    }
}

class KMP {
    public List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int[] lps = computeLPS(pattern);

        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            if (j == pattern.length()) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return matches;
    }

    private int[] computeLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length > 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}

class LCS {
    public String findLCS(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        StringBuilder lcs = new StringBuilder();
        int i = str1.length(), j = str2.length();
        while (i > 0 && j > 0) {
            if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                lcs.append(str1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return lcs.reverse().toString();
    }
}