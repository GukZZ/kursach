document.addEventListener('DOMContentLoaded', function () {
    fetchStudents();

    document.getElementById('add-student-form').addEventListener('submit', function (e) {
        e.preventDefault();
        addStudent();
    });

    document.getElementById('update-student-form').addEventListener('submit', function (e) {
        e.preventDefault();
        updateStudent();
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
                li.textContent = `ID: ${student.id}, Name: ${student.name}, Age: ${student.age}`;
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
            'Content-Type': 'application/json; charset=utf-8'
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

function updateStudent() {
    const id = document.getElementById('update-id').value;
    const name = document.getElementById('update-name').value;
    const age = document.getElementById('update-age').value;

    fetch('/students', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: JSON.stringify({ id, name, age })
    })
        .then(response => response.json())
        .then(result => {
            if (result.status === 'success') {
                fetchStudents();
                document.getElementById('update-student-form').reset();
            } else {
                alert('Failed to update student');
            }
        });
}

function deleteStudent(id) {
    fetch('/students', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
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
