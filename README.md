# tokyo-olimpics
	  
	  Serviço para consulta / cadastro da agenda de competições para as olimpiadas de tokyo.
    
    Endpoints disponibilizados:
    ------------------------------------------------------------------------------------------------------
	  http://localhost:8080/competition-service/competition
    method:POST
    
    Inserção de competição
    
	  Regras de validação:
	  
	  1. Todos os campos devem estar preenchidos;
	  2. Os objetos modality e local podem ter ou o id, ou a description;
	  3. A data de início não pode ser maior que a data de fim;
	  4. Os competidores só podem pertencer ao mesmo país quando a phase for F ou SF
	  5. As modalidades, locais, paises e etapas devem ser válidas
	  6. Em um mesmo dia, um local pode ter até 4 eventos
	  7. No mesmo momento não pode haver uma competição da mesma modalidade e local
	  8. O tempo mínimo de cada evento é de 30 minutos
	  
	  
	  Exemplo de uma nova competição:
	  {
	        "modality": {
	            "description": "Basketball"
	        },
	        "local": {
	            "id": 1,
	            "description": "Enoshima"
	        },
	        "initDate": "2017-15-01T19:46:00.000+02:00",
	        "endDate": "2017-15-01T20:45:00.000+02:00",
	        "competitors": {
	            "competitor1": "AR",
	            "competitor2": "BR"
	        },
	        "phase": "EL"
	    }
      
    ------------------------------------------------------------------------------------------------------  
    http://localhost:8080/competition-service/competition
    method:GET
    
    Busca todas as competições ordenadas por data. É possível filtrar por modalidade
    
    Exemplo: http://localhost:8080/competition-service/competition?modality=Soccer
    ------------------------------------------------------------------------------------------------------
    http://localhost:8080/competition-service/competition/{id}
    method:GET
    
    Localiza um recurso por seu ID
    ------------------------------------------------------------------------------------------------------
    http://localhost:8080/competition-service/country
    method:GET
    Header:Accept-Language
    
    Localiza todos os países participantes. 
    pt-BR:
     {
        "code": "BR",
        "description": "Brasil"
    },
    {
        "code": "AR",
        "description": "Argentina"
    }
    en-US:
    {
        "code": "FR",
        "description": "France"
    },
    {
        "code": "ES",
        "description": "Spain"
    }
    ------------------------------------------------------------------------------------------------------
    http://localhost:8080/competition-service/local
    method:GET
    Header:Accept-Language
    
    Disponibiliza todos os locais onde ocorrerão os eventos. 
    
    [
    {
        "id": 1,
        "description": "Kokugikan Arena"
    },
    {
        "id": 2,
        "description": "Nippon Budokan"
    },
    {
    ]

	  ------------------------------------------------------------------------------------------------------
    
    http://localhost:8080/competition-service/modality
    method:GET
    
    Disponibiliza todas as modalidades disputadas
    
    [
    {
        "id": 1,
        "description": "Soccer"
    },
    {
        "id": 2,
        "description": "Basketball"
    }
    ]
    
    ------------------------------------------------------------------------------------------------------
    http://localhost:8080/competition-service/modality
    method:GET
    Header:Accept-Language
    
    Disponibiliza todas as etapas de cada competição
    
    [
    {
        "code": "EL",
        "description": "Elimination"
    },
    {
        "code": "F8",
        "description": "Eighth-finals"
    },
    {
        "code": "F4",
        "description": "Quarterfinals"
    },
    {
        "code": "SF",
        "description": "Semifinals"
    },
    {
        "code": "F",
        "description": "Final"
    }
]
    
