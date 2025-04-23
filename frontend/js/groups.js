document.addEventListener("DOMContentLoaded", () => {
    const groupList = document.getElementById("groupList");
    const createGroupForm = document.getElementById("createGroupForm");
    const groupNameInput = document.getElementById("groupNameInput");
    const memberCheckboxes = document.querySelectorAll(".member-checkbox");

    // Fetch and display groups
    const fetchGroups = async () => {
        try {
            const response = await fetch("/backend/group/fetch_groups.php", { method: "GET" });
            const groups = await response.json();

            if (response.ok) {
                displayGroups(groups);
            } else {
                alert(groups.message || "Failed to fetch groups.");
            }
        } catch (error) {
            console.error("Error fetching groups:", error);
        }
    };

    // Display groups in the sidebar
    const displayGroups = (groups) => {
        groupList.innerHTML = ""; // Clear existing groups
        groups.forEach((group) => {
            const groupItem = document.createElement("li");
            groupItem.classList.add("group-item");
            groupItem.textContent = group.name;
            groupItem.dataset.groupId = group.id;

            // Navigate to group chat on click
            groupItem.addEventListener("click", () => {
                localStorage.setItem("group_id", group.id);
                window.location.href = "chat.html";
            });

            groupList.appendChild(groupItem);
        });
    };

    // Create a new group
    createGroupForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const groupName = groupNameInput.value.trim();
        const selectedMembers = Array.from(memberCheckboxes)
            .filter((checkbox) => checkbox.checked)
            .map((checkbox) => checkbox.value);

        if (!groupName || selectedMembers.length === 0) {
            alert("Please provide a group name and select at least one member.");
            return;
        }

        try {
            const response = await fetch("/backend/group/create_group.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ group_name: groupName, members: selectedMembers }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Group created successfully!");
                fetchGroups(); // Refresh group list
                createGroupForm.reset(); // Clear form
            } else {
                alert(result.message || "Failed to create group.");
            }
        } catch (error) {
            console.error("Error creating group:", error);
        }
    });

    // Add a member to a group
    const addMemberToGroup = async (groupId, userId) => {
        try {
            const response = await fetch("/backend/group/add_member.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ group_id: groupId, user_id: userId }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Member added successfully!");
                fetchGroups(); // Refresh group list
            } else {
                alert(result.message || "Failed to add member.");
            }
        } catch (error) {
            console.error("Error adding member:", error);
        }
    };

    // Remove a member from a group
    const removeMemberFromGroup = async (groupId, userId) => {
        try {
            const response = await fetch("/backend/group/remove_member.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ group_id: groupId, user_id: userId }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Member removed successfully!");
                fetchGroups(); // Refresh group list
            } else {
                alert(result.message || "Failed to remove member.");
            }
        } catch (error) {
            console.error("Error removing member:", error);
        }
    };

    // Rename a group
    const renameGroup = async (groupId, newName) => {
        try {
            const response = await fetch("/backend/group/rename_group.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ group_id: groupId, new_name: newName }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Group renamed successfully!");
                fetchGroups(); // Refresh group list
            } else {
                alert(result.message || "Failed to rename group.");
            }
        } catch (error) {
            console.error("Error renaming group:", error);
        }
    };

    // Set a nickname for a user in a group
    const setNickname = async (groupId, userId, nickname) => {
        try {
            const response = await fetch("/backend/group/set_nickname.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ group_id: groupId, user_id: userId, nickname }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Nickname set successfully!");
            } else {
                alert(result.message || "Failed to set nickname.");
            }
        } catch (error) {
            console.error("Error setting nickname:", error);
        }
    };

    // Initial fetch of groups
    fetchGroups();
});