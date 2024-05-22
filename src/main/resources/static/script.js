document.addEventListener('DOMContentLoaded', function () {
    fetchStudents();

    document.getElementById('add-student-form').addEventListener('submit', function (e) {
        e.preventDefault();
        addStudent();
    });

    document.getElementById('filter-button').addEventListener('click', function () {
        fetchStudents();
    });

    document.getElementById('edit-student-form').addEventListener('submit', function (e) {
        e.preventDefault();
        updateStudent();
    });

    document.getElementById('cancel-edit').addEventListener('click', function () {
        document.getElementById('edit-student-form').style.display = 'none';
    });
});

function fetchStudents() {
    const filterName = document.getElementById('filter-name').value;
    let url = '/students';
    if (filterName) {
        // Updated query parameter to 'first_name' to match the server's expected parameter
        url += `?first_name=${encodeURIComponent(filterName)}`;
    }

    fetch(url)
        .then(response => response.json())
        .then(students => {
            const studentList = document.getElementById('students');
            studentList.innerHTML = '';
            if (Array.isArray(students)) {
                students.forEach(student => {
                    const li = document.createElement('li');
                    li.textContent = `ID: ${student.id}, ${student.firstName} ${student.lastName} ${student.middleName}, Группа: ${student.groupName}, Возраст: ${student.age}`;
                    const editButton = document.createElement('button');
                    editButton.textContent = 'Редактировать';
                    editButton.onclick = () => showEditForm(student);
                    const deleteButton = document.createElement('button');
                    deleteButton.textContent = 'Удалить';
                    deleteButton.onclick = () => deleteStudent(student.id);
                    li.appendChild(editButton);
                    li.appendChild(deleteButton);
                    studentList.appendChild(li);
                });
            } else {
                console.error('Ошибка при загрузке студентов:', students);
            }
        })
        .catch(error => {
            console.error('Ошибка при загрузке студентов:', error);
        });
}


function addStudent() {
    const firstName = document.getElementById('first-name').value;
    const lastName = document.getElementById('last-name').value;
    const middleName = document.getElementById('middle-name').value;
    const groupName = document.getElementById('group-name').value;
    const age = parseInt(document.getElementById('age').value, 10);

    fetch('/students', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ firstName, lastName, middleName, groupName, age })
    })
        .then(response => response.json())
        .then(result => {
            if (result.status === 'success') {
                fetchStudents();
                document.getElementById('add-student-form').reset();
            } else {
                alert('Не удалось добавить студента');
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
                alert('Не удалось удалить студента');
            }
        });
}

function showEditForm(student) {
    document.getElementById('edit-id').value = student.id;
    document.getElementById('edit-first-name').value = student.firstName;
    document.getElementById('edit-last-name').value = student.lastName;
    document.getElementById('edit-middle-name').value = student.middleName;
    document.getElementById('edit-group-name').value = student.groupName;
    document.getElementById('edit-age').value = student.age;
    document.getElementById('edit-student-form').style.display = 'block';
}

function updateStudent() {
    const id = document.getElementById('edit-id').value;
    const firstName = document.getElementById('edit-first-name').value;
    const lastName = document.getElementById('edit-last-name').value;
    const middleName = document.getElementById('edit-middle-name').value;
    const groupName = document.getElementById('edit-group-name').value;
    const age = parseInt(document.getElementById('edit-age').value, 10);

    fetch('/students', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id, firstName, lastName, middleName, groupName, age })
    })
        .then(response => response.json())
        .then(result => {
            if (result.status === 'success') {
                fetchStudents();
                document.getElementById('edit-student-form').style.display = 'none';
            } else {
                alert('Не удалось обновить данные студента');
            }
        });
}
