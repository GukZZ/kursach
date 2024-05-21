document.addEventListener('DOMContentLoaded', function () {
    fetchStudents();

    document.getElementById('add-student-form').addEventListener('submit', function (e) {
        e.preventDefault();
        addStudent();
    });
});

function fetchStudents() {
    fetch('/students')
        .then(response => response.json())
        .then(students => {
            const studentList = document.getElementById('students');
            studentList.innerHTML = '';
            students.forEach(student => {
                const li = document.createElement('li');
                li.textContent = `${student.name}, Age: ${student.age}`;
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Delete';
                deleteButton.onclick = () => deleteStudent(student.id);
                li.appendChild(deleteButton);
                studentList.appendChild(li);
            });
        });
}

function addStudent() {
    const name = document.getElementById('name').value;
    const age = document.getElementById('age').value;

    fetch('/students', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, age })
    })
        .then(response => response.json())
        .then(result => {
            if (result.status === 'success') {
                fetchStudents();
                document.getElementById('add-student-form').reset();
            } else {
                alert('Failed to add student');
            }
        });
}

function deleteStudent(id) {
    fetch('/students', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id })
    })
        .then(response => response.json())
        .then(result => {
            if (result.status === 'success') {
                fetchStudents();
            } else {
                alert('Failed to delete student');
            }
        });
}
