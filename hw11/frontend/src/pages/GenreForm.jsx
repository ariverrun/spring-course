import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

function GenreForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [name, setName] = useState('');

  useEffect(() => {
    if (isEdit) {
      fetch(`/api/v1/genre/${id}`)
        .then(response => response.json())
        .then(data => setName(data.name));
    }
  }, [id, isEdit]);

  const handleSubmit = (e) => {
    e.preventDefault();
    
    const url = isEdit ? `/api/v1/genre/${id}` : '/api/v1/genre';
    const method = isEdit ? 'PUT' : 'POST';
    
    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    }).then(() => navigate('/genres'));
  };

  return (
    <div>
      <h2>{isEdit ? 'Редактировать жанр' : 'Добавить жанр'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Название:</label><br />
          <input type="text" value={name} onChange={e => setName(e.target.value)} required />
        </div>
        <button type="submit">Сохранить</button>
        <Link to="/genres">Отмена</Link>
      </form>
    </div>
  );
}

export default GenreForm;