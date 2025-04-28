# import libraries
import tkinter as tk
import heapq
import math

# Define Node class to represent each cell in the map
class Node:
    def __init__(self, position, parent=None):
        self.position = position  # Position of the node (x, y)
        self.parent = parent  # Parent node in the path
        self.g = 0  # Cost from start to this node
        self.h = 0  # Heuristic cost to goal
        self.f = 0  # Total cost (g + h)

# Define the PriorityQueue
class PriorityQueue:
    def __init__(self):
        self.elements = []

    def is_empty(self):
        return not self.elements

    def put(self, item, priority):
        # Add an item with its priority
        heapq.heappush(self.elements, (priority, item))

    def get(self):
        # Remove and return the item with the highest priority
        return heapq.heappop(self.elements)[1]


# Define the Map class to represent the virtual world
class Map:
    # Dimensions of the map
    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.blocks = []
        self.cost = {}

    # Check if the coordinates are within the map
    def in_bounds(self, id):
        (x, y) = id
        return 0 <= x < self.width and 0 <= y < self.height

    # Check if the cell is not blocked
    def passable(self, id):
        return id not in self.blocks

    # Determine possible moves
    def neighbors(self, id):
        (x, y) = id
        if x % 2 == 0:
            neighbors = [(x, y - 1), (x, y + 1), (x - 1, y),
                         (x + 1, y), (x - 1, y + 1), (x + 1, y + 1)]
        else:
            neighbors = [(x, y - 1), (x, y + 1), (x - 1, y),
                         (x + 1, y), (x - 1, y - 1), (x + 1, y - 1)]
        neighbors = filter(self.in_bounds, neighbors)
        neighbors = filter(self.passable, neighbors)
        return neighbors

    # Define the cost of each type of cell
    def calculate_cost(self, current_node, next_node, traps, rewards):
        move_cost = 1

        # Define trap effects
        trap_effects = {
            'trap1': lambda cost: cost * 2,
            'trap2': lambda cost: cost * 2,
            'trap3': lambda cost: cost + 2,
            'trap4': lambda cost: float('inf')
        }
        if next_node in traps:
            trap_effect = traps[next_node]
            if trap_effect in trap_effects:
                move_cost = trap_effects[trap_effect](move_cost)
        # Define reward effects
        reward_effects = {
            'reward1': lambda cost: cost / 2,
            'reward2': lambda cost: cost / 2
        }
        if next_node in rewards:
            reward_effect = rewards[next_node]
            if reward_effect in reward_effects:
                move_cost = reward_effects[reward_effect](move_cost)

        return move_cost

# Function for single target search using A* algorithm
def single_target_search(grid, start, goals, traps, rewards, current_cost):
    frontier = PriorityQueue()
    # Add the start node to the frontier with priority 0
    frontier.put(start, 0)
    source_node = {}
    source_node[start] = None
    current_cost = {start: current_cost[start]}
    visited = set()

    while not frontier.is_empty():  # Loop until the frontier is empty
        current = frontier.get()  # Get the node with the highest priority
        visited.add(current)  # Mark the current node as visited

        if current in goals:  # Check if the current node is a goal
            # Return the result if goal is reached
            return source_node, current_cost, {current}

        for next in grid.neighbors(current):  # Iterate over the neighbors
            # Calculate the new cost
            new_cost = current_cost[current] + \
                grid.calculate_cost(current, next, traps, rewards)
            # Check if the next node has a lower cost
            if next not in current_cost or new_cost < current_cost[next]:
                current_cost[next] = new_cost  # Update the cost
                priority = new_cost + min(heuristic(next, goal)
                                          # Calculate the priority
                                          for goal in goals)
                # Add the next node to the frontier
                frontier.put(next, priority)
                source_node[next] = current  # Update the source node

    reachable_treasures = set()
    return source_node, current_cost, reachable_treasures

# Define the Heuristic function for A* search using Euclidean distance
def heuristic(current, goal):
    return math.sqrt((current[0] - goal[0])**2 + (current[1] - goal[1])**2)

# Function for A* search to find the path to all treasures
def a_star_search(grid, start, treasures, traps, rewards):
    all_treasures = set(treasures)
    current_position = start
    path = []  # List to store the path
    current_cost = {start: 0}  # Dictionary to store the current cost

    while all_treasures:  # Loop until all treasures are found
        source_node, new_current_cost, reachable_treasures = single_target_search(
            grid, current_position, all_treasures, traps, rewards, current_cost)

        if not reachable_treasures:  # Check if no reachable treasures
            print("\nUnable to reach more treasures...")
            break  # Break the loop if no more treasures are reachable

        next_treasure = min(reachable_treasures, key=lambda t: new_current_cost.get(
            t, float('inf')))  # Find the next treasure with the lowest cost
        if new_current_cost.get(next_treasure, float('inf')) == float('inf'):
            break  # Break the loop if the next treasure is not reachable

        # Reconstruct the path to the next treasure
        path_segment = reconstruct_path(
            source_node, current_position, next_treasure)
        path.extend(path_segment[1:])  # Add the path segment to the main path
        current_position = next_treasure  # Update the current position
        # Remove the found treasure from the set
        all_treasures.remove(next_treasure)
        current_cost = new_current_cost  # Update the current cost

    return path, current_cost

# Function to reconstruct the path from start to goal
def reconstruct_path(source_node, start, goal):
    current = goal
    path = []
    while current != start:  # Loop until the start node is reached
        path.append(current)  # Add the current node to the path
        current = source_node[current]  # Move to the parent node
    path.append(start)  # Add the start node to the path
    path.reverse()  # Reverse the path to get the correct order
    return path

# Function to calculate the points of a hexagon for drawing
def hex_points(center_x, center_y, size):
    # Calculate the angles for the hexagon points
    angles = [math.radians(60 * i) for i in range(6)]
    points = [(center_x + size * math.cos(angle), center_y +
               size * math.sin(angle)) for angle in angles]

    return points

# Define the App class for the GUI application
class App(tk.Tk):
    trap_symbols = {'trap1': '⊖', 'trap2': '⊕', 'trap3': '⊗', 'trap4': '⊘'}
    reward_symbols = {'reward1': '⊞', 'reward2': '⊠'}
    symbol_fonts = ('Helvetica', 20, 'bold')

    def __init__(self, map, start, traps, rewards, treasures):
        super().__init__()  # Initialize the superclass
        self.title("~ Virtual Treasure Hunt ~")  # Set the title of the window
        # Create a canvas for drawing
        self.canvas = tk.Canvas(self, width=500, height=400, bg='lightblue')
        self.canvas.pack()  # Pack the canvas
        self.map = map  # Map object
        self.start = start  # Start position
        self.traps = traps  # Traps dictionary
        self.rewards = rewards  # Rewards dictionary
        self.treasures = treasures  # List of treasures
        self.cell_size = 30  # Size of each cell
        self.draw_map()  # Draw the initial map
        self.path = []  # List to store the path
        self.current_cost = 0  # Initialize the current cost
        self.current_step = 0  # Initialize the current step
        self.run_search()  # Run the A* search algorithm
        self.status_label = tk.Label(self, text="Pathfinding...", font=('Helvetica', 12))
        self.status_label.pack()

    def draw_map(self):
        for y in range(self.map.height):
            for x in range(self.map.width):
                # Calculate the x-coordinate of the cell center
                center_x = x * self.cell_size * 1.5 + 50
                # Calculate the y-coordinate of the cell center
                center_y = y * self.cell_size * math.sqrt(3) + 50
                if x % 2 == 0:
                    # Adjust the y-coordinate for even columns
                    center_y += self.cell_size * math.sqrt(3) / 2
                # Get the points of the hexagon
                points = hex_points(center_x, center_y, self.cell_size)
                label = ''

                # Determine the color and label of the cell
                if (x, y) in self.map.blocks:
                    cell_color = 'dark grey'
                elif (x, y) == self.start:
                    cell_color = 'green'
                    label = 'S'
                elif (x, y) in self.traps:
                    cell_color = 'magenta'
                    label = self.trap_symbols[self.traps[(x, y)]]
                elif (x, y) in self.rewards:
                    cell_color = 'cyan'
                    label = self.reward_symbols[self.rewards[(x, y)]]
                elif (x, y) in self.treasures:
                    cell_color = 'gold'
                else:
                    cell_color = 'white'

                self.canvas.create_polygon(
                    points, fill=cell_color, outline='black', width=2)
                if label:
                    self.canvas.create_text(
                        center_x, center_y, text=label, fill='black', font=self.symbol_fonts)

    def run_search(self):
        self.path, self.current_cost = a_star_search(
            # Run the A* search algorithm
            self.map, self.start, self.treasures, self.traps, self.rewards)
        self.current_step = 0  # Initialize the current step
        self.draw_path()  # Draw the path

    def draw_path(self):
        # Check if there are more steps in the path
        if self.current_step < len(self.path):
            (x, y) = self.path[self.current_step]  # Get the current step
            # Calculate the x-coordinate of the cell center
            center_x = x * self.cell_size * 1.5 + 50
            # Calculate the y-coordinate of the cell center
            center_y = y * self.cell_size * math.sqrt(3) + 50
            if x % 2 == 0:
                # Adjust the y-coordinate for even columns
                center_y += self.cell_size * math.sqrt(3) / 2
            # Get the points of the hexagon
            points = hex_points(center_x, center_y, self.cell_size)
            # Draw the hexagon for the path
            self.canvas.create_polygon(
                points, fill='green', outline='black', tags="path")

            if (x, y) in self.treasures:  # Check if the current step is a treasure
                # Draw the treasure symbol
                self.canvas.create_text(
                    center_x, center_y, text='🧰', fill='black', font=self.symbol_fonts)
            if (x, y) in self.rewards:  # Check if the current step is a reward
                reward_type = self.rewards[(x, y)]
                label = self.reward_symbols[reward_type]
                # Draw the reward symbol
                self.canvas.create_text(
                    center_x, center_y, text=label, fill='black', font=self.symbol_fonts)

            self.current_step += 1  # Increment the current step
            self.after(500, self.draw_path)  # Animation delay for the drawing

        else:
            print("\nEND!\nYou have reached the goal.\n")
            self.status_label.config(text="Complete!")

# Main function to run the application
def main():
    width = 10
    height = 6
    map = Map(width, height)  # Set map dimensions
    start = (0, 0)

    # Define blocks positions
    map.blocks = [(2, 2), (4, 2), (8, 1), (0, 3), (3, 3),
                  (6, 3), (4, 4), (6, 4), (7, 4)]
    # Define treasure positions
    treasures = [(3, 4), (4, 1), (7, 3), (9, 3)]
    # Define traps positions
    traps = {(8, 2): 'trap1', (1, 1): 'trap2', (2, 4): 'trap2',
             (6, 1): 'trap3', (5, 3): 'trap3', (3, 1): 'trap4'}
    # Define rewards positions
    rewards = {(4, 0): 'reward1', (1, 3): 'reward1',
               (7, 2): 'reward2', (5, 5): 'reward2'}

    print("\nVirtual Treasure Hunt using A* search\n")
    print("-----------------------------------------------------")
    print("Goal: Collect all the treasures with the optimum route")
    print("-----------------------------------------------------")

    app = App(map, start, traps, rewards, treasures)
    app.mainloop()  # Run the application

    print("Path: ")
    print(app.path)
    total_cost = app.current_step
    print("\nTotal path cost: ", total_cost)


# Run the main function if the script is executed
if __name__ == '__main__':
    main()
