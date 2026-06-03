import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

function BookForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  
  const [title, setTitle] = useState('');
  const [authorId, setAuthorId] = useState('');
  const [genreIds, setGenreIds] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [genres, setGenres] = useState([]);

  useEffect(() => {
    Promise.all([
      fetch('/api/v1/author').then(r => r.json()),
      fetch('/api/v1/genre').then(r => r.json())
    ]).then(([authorsData, genresData]) => {
      setAuthors(authorsData);
      setGenres(genresData);
    });

    if (isEdit) {
      fetch(`/api/v1/book/${id}`)
        .then(response => response.json())
        .then(data => {
          setTitle(data.title);
          setAuthorId(data.author.id);
          setGenreIds(data.genres.map(g => g.id));
        });
    }
  }, [id, isEdit]);

  const handleGenreChange = (e) => {
    const options = e.target.options;
    const selected = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) {
        selected.push(parseInt(options[i].value));
      }
    }
    setGenreIds(selected);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    const bookData = { title, authorId: parseInt(authorId), genreIds };
    
    const url = isEdit ? `/api/v1/book/${id}` : '/api/v1/book';
    const method = isEdit ? 'PUT' : 'POST';
    
    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(bookData)
    }).then(() => navigate('/books'));
  };

  return (
    <div>
      <h2>{isEdit ? 'Редактировать книгу' : 'Добавить книгу'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Название:</label><br />
          <input type="text" value={title} onChange={e => setTitle(e.target.value)} required />
        </div>
        <div>
          <label>Автор:</label><br />
          <select value={authorId} onChange={e => setAuthorId(e.target.value)} required>
            <option value="">Выберите автора</option>
            {authors.map(a => <option key={a.id} value={a.id}>{a.fullName}</option>)}
          </select>
        </div>
        <div>
          <label>Жанры (Ctrl+выбор):</label><br />
          <select multiple value={genreIds} onChange={handleGenreChange}>
            {genres.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
          </select>
        </div>
        <button type="submit">Сохранить</button>
        <Link to="/books">Отмена</Link>
      </form>
    </div>
  );
}

export default BookForm;