import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

function AuthorForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [fullName, setFullName] = useState('');

  useEffect(() => {
    if (isEdit) {
      fetch(`/api/v1/author/${id}`)
        .then(response => response.json())
        .then(data => setFullName(data.fullName));
    }
  }, [id, isEdit]);

  const handleSubmit = (e) => {
    e.preventDefault();
    
    const url = isEdit ? `/api/v1/author/${id}` : '/api/v1/author';
    const method = isEdit ? 'PUT' : 'POST';
    
    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fullName })
    }).then(() => navigate('/authors'));
  };

  return (
    <div>
      <h2>{isEdit ? 'Редактировать автора' : 'Добавить автора'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Имя:</label><br />
          <input type="text" value={fullName} onChange={e => setFullName(e.target.value)} required />
        </div>
        <button type="submit">Сохранить</button>
        <Link to="/authors">Отмена</Link>
      </form>
    </div>
  );
}

export default AuthorForm;